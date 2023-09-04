package com.springles.service.impl;

import com.springles.domain.constants.Level;
import com.springles.domain.dto.member.*;
import com.springles.domain.entity.*;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.jwt.JwtTokenUtils;
import com.springles.repository.*;
import com.springles.repository.MemberGameInfoJpaRepository;
import com.springles.service.MemberService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberJpaRepository memberRepository;
    private final RefreshTokenRedisRepository memberRedisRepository;
    private final BlackListTokenRedisRepository blackListTokenRedisRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final MemberGameInfoJpaRepository memberGameInfoJpaRepository;
    private final GameRecordJpaRepository gameRecordJpaRepository;
    private final MemberRecordJpaRepository memberRecordJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final JavaMailSender javaMailSender;

    // 사용자 정보 가져오기
    @Override
    public MemberInfoResponse getUserInfo(String authHeader) {

        String memberName = jwtTokenUtils.parseClaims(authHeader).getSubject();   // AccessToken으로 닉네임 받아오기

        return MemberInfoResponse.of(memberRepository.findByMemberName(memberName) // 닉네임으로 info dto 반환
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)));
    }

    @Override
    public String signUp(MemberCreateRequest memberDto) {

        // 아이디 기 사용 여부 체크
        if (memberExists(memberDto.getMemberName())) {
            throw new CustomException(ErrorCode.EXIST_MEMBERNAME);
        }

        // 비밀번호와 비밀번호 확인 값 일치 여부 체크
        if (!memberDto.getPassword().equals(memberDto.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        // 회원가입 완료
        memberRepository.save(memberDto.newMember(passwordEncoder));

        // 게임기록 생성
        memberRecordJpaRepository.save(newMemberRecord(memberDto.getMemberName()));

        return MemberCreateRequest.fromEntity(memberDto.newMember(passwordEncoder)).toString();
    }

    @Override
    public String updateInfo(MemberUpdateRequest memberDto, String accessToken) {

        String memberName = jwtTokenUtils.parseClaims(accessToken).getSubject();

        // 헤더의 회원정보가 존재하는 회원정보인지 체크
        Optional<Member> optionalMember = memberRepository.findByMemberName(memberName);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 탈퇴한 회원인지 체크
        if (optionalMember.get().getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_MEMBER);
        }

        Member updateMember = optionalMember.get();

        try {
            // 이메일이 변경되었는지 체크 (기존 이메일의 null 여부에 따른 분기)
            if (!updateMember.getEmail().equals(memberDto.getEmail())) {
                updateMember.setEmail(memberDto.getEmail());
            }
        } catch (NullPointerException e) {
            updateMember.setEmail(memberDto.getEmail());
        }

        // 비밀번호와 비밀번호 확인 값 일치여부 체크
        if (!memberDto.getPassword().equals(memberDto.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        updateMember.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        memberRepository.save(updateMember);

        return updateMember.toString();
    }

    @Override
    @Transactional
    public void signOut(MemberDeleteRequest memberDto, String accessToken) {

        String memberName = jwtTokenUtils.parseClaims(accessToken).getSubject();

        // 헤더의 회원정보가 존재하는 회원정보인지 체크
        Optional<Member> optionalMember = memberRepository.findByMemberName(memberName);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 탈퇴한 회원인지 체크
        if (optionalMember.get().getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_MEMBER);
        }

        // 입력한 비밀번호와 기존 비밀번호 일치 여부 체크
        if (!(passwordEncoder.matches(memberDto.getPassword(), optionalMember.get().getPassword()))) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        memberRepository.deleteByMemberName(memberName);
    }


    @Override
    @Transactional
    public MemberLoginResponse login(MemberLoginRequest memberDto) {

        // 아이디에 해당하는 회원정보가 있는지 확인
        Optional<Member> optionalMember = memberRepository.findByMemberName(memberDto.getMemberName());
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 탈퇴한 회원인지 체크
        if (optionalMember.get().getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_MEMBER);
        }

        // 회원정보를 가져와서 해당 비밀번호와 저장된 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(memberDto.getPassword(), optionalMember.get().getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        // 기존에 생성된 refreshToken이 있을 경우 삭제
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRedisRepository.findByMemberName(memberDto.getMemberName());
        if (optionalRefreshToken.isPresent()) {
            refreshTokenRedisRepository.deleteById(optionalRefreshToken.get().getId());
        }

        // accessToken 생성
        String accessToken = jwtTokenUtils.generatedToken(memberDto.getMemberName());

        // refreshToken 생성
        RefreshToken refreshToken = jwtTokenUtils.generaedRefreshToken(memberDto.getMemberName());

        // refreshToken 저장
        memberRedisRepository.save(refreshToken);

        // toString()으로 반환할 경우 접근하기 어려워서 수정했습니다.
        return MemberLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberName(memberDto.getMemberName())
                .build();

//        return MemberLoginResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .memberName(memberDto.getMemberName())
//                .build()
//                .toString();
    }

    @Override
    @Transactional
    public void logout(String authHeader) {
        String memberName = jwtTokenUtils.parseClaims(authHeader.split(" ")[1]).getSubject();
        Date rawExpiration = jwtTokenUtils.parseClaims(authHeader.split(" ")[1]).getExpiration();

        // 헤더의 회원정보가 존재하는 회원정보인지 체크
        Optional<Member> optionalMember = memberRepository.findByMemberName(memberName);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 탈퇴한 회원인지 체크
        if (optionalMember.get().getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_MEMBER);
        }
        // refreshToken 삭제
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRedisRepository.findByMemberName(memberName);
        if (optionalRefreshToken.isEmpty()) {
            throw new CustomException(ErrorCode.NO_JWT_TOKEN);
        }
        refreshTokenRedisRepository.deleteById(optionalRefreshToken.get().getId());

        // 블랙리스트에 저장
        BlackListToken blackListToken = BlackListToken.builder()
                .accessToken(authHeader.split(" ")[1])
                // accessToken의 남은 유효시간만큼만 저장
                .expiration((rawExpiration.getTime() - Date.from(Instant.now()).getTime()) / 1000)
                .build();
        log.info("token Expiration : " + rawExpiration);

        blackListTokenRedisRepository.save(blackListToken);
    }


    @Override
    public String vertificationId(MemberVertifIdRequest memberDto) {

        List<Member> memberList = memberRepository.findAllByEmail(memberDto.getEmail());
        if (memberList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_INPUT_VALUE_MEMBER);
        }

        // 해당 email로 가입된 id 개수
        int idCount = memberList.size();

        // 해당 email로 가입된 id 목록 (response용)
        List<String> idList = new ArrayList<>();

        // 해당 email로 가입된 id 목록 (메일 전송용)
        String idListStr = "";

        for (Member member : memberList) {
            idList.add(member.getMemberName());
            idListStr += (member.getMemberName() + "\n");
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            // 수신자
            mimeMessageHelper.setTo(memberDto.getEmail());
            // 제목
            mimeMessageHelper.setSubject("[CHATFIA] 아이디 찾기 결과 안내드립니다.");
            // 본문 (추후 thymeleaf로 구현 예정)
            mimeMessageHelper.setText(
                    "<div>"
                            + "<table style = \"width:100% margin:0; padding:0; min-width:100%\">"
                            + "<tbody>"
                            + "<tr>"
                            + "<td align=\"center\">"
                            + "<div>"
                            + "<h2>아이디 찾기 결과 안내</h2>"
                            + "<p>입력한 이메일로 가입된 아이디가 총 " + idCount + "개 있습니다.</p>"
                            + "<div>"
                            + "<pre style=\"width:320px; padding:16px 24px;border:1px solid #EEEEEE;background-color:#F4F4F4;border-radius:3px;margin-bottom:24px\">"
                            + idListStr
                            + "</pre>"
                            + "</div>"
                            + "<div>"
                            + "</td>"
                            + "</tr>"
                            + "</tbody>"
                            + "</table>"
                            + "</div>"
                    , true);

            javaMailSender.send(mimeMessage);

            return "memberName : " + idList + ", email : " + memberDto.getEmail();
        } catch (MessagingException e) {
            log.error("{}", e.getClass());
            log.error("{}", e.getMessage());
            throw new CustomException(ErrorCode.FAIL_SEND_MEMBER_ID);
        }
    }

    @Override
    public String vertificationPw(MemberVertifPwRequest memberDto) {
        String memberName = memberDto.getMemberName();
        String email = memberDto.getEmail();
        String tempPassword = randomPassword();

        Optional<Member> optionalMember = memberRepository.findByMemberNameAndEmail(memberName, email);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_INPUT_VALUE_MEMBER);
        }

        // 비밀번호를 임시비밀번호로 변경
        Member updateMember = optionalMember.get();
        updateMember.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(updateMember);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            // 수신자
            mimeMessageHelper.setTo(email);
            // 제목
            mimeMessageHelper.setSubject("[CHATFIA] 임시 비밀번호 안내드립니다.");
            // 본문 (추후 thymeleaf로 구현 예정)
            mimeMessageHelper.setText(
                    "<div>"
                            + "<table style=\"width:100%; margin:0; padding:0; min-width:100%\">"
                            + "<tbody>"
                            + "<tr>"
                            + "<td align=\"center\">"
                            + "<div>"
                            + "<h2>임시 비밀번호 안내</h2>"
                            + "<b>" + memberName + "</b>님의 임시비밀번호는 다음과 같습니다."
                            + "<div>"
                            + "<pre style=\"width:320px; padding:16px 24px; border:1px solid #EEEEEE; background-color:#F4F4F4; border-radius:3px; margin-bottom:24px\">"
                            + tempPassword
                            + "</pre>"
                            + "</div>"
                            + "<p style=\"color: #767678; font-size: 12px\"> (!) 회원가입 시 등록한 정보는 [마이페이지 > 회원 정보 관리]에서 변경하실 수 있습니다.</p>"
                            + "<div>"
                            + "</td>"
                            + "</tr>"
                            + "</tbody>"
                            + "</table>"
                            + "</div>"
                    , true
            );

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.error("{}", e.getClass());
            log.error("{}", e.getMessage());
            throw new CustomException(ErrorCode.FAIL_SEND_MEMBER_PW);
        }

        return "memberName : " + memberName
                + ", password : " + passwordEncoder.encode(randomPassword())
                + ", email : " + email;
    }

    // 임시 비밀번호 생성
    @Override
    public String randomPassword() {
        // 임시 비밀번호
        String tempPassword = "";
        char[] charSet = {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                '!', '@', '#', '$', '%', '^', '&', '*'
        };

        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * charSet.length);
            tempPassword += charSet[index];
        }

        return tempPassword;
    }

    @Override
    public MemberProfileResponse createProfile(MemberProfileCreateRequest memberDto, String accessToken) {

        String memberName = jwtTokenUtils.parseClaims(accessToken).getSubject();

        // 헤더의 회원정보가 존재하는 회원정보인지 체크
        Optional<Member> optionalMember = memberRepository.findByMemberName(memberName);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 탈퇴한 회원인지 체크
        if (optionalMember.get().getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_MEMBER);
        }

        Optional<MemberGameInfo> optionalMemberGameInfo = memberGameInfoJpaRepository.findByMemberId(optionalMember.get().getId());

        // 이미 프로필이 설정되어 있는지 체크
        if (optionalMemberGameInfo.isPresent()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
        }

        MemberProfileCreateRequest newMemberInfo = new MemberProfileCreateRequest();
        MemberGameInfo newMemberGameInfo = memberGameInfoJpaRepository.save(newMemberInfo.newMemberGameInfo(memberDto, optionalMember.get().getId()));
        return MemberProfileResponse.of(newMemberGameInfo, optionalMember.get().getId());
    }


    @Override
    public MemberProfileResponse updateProfile(MemberProfileUpdateRequest memberDto, String accessToken) {
        String memberName = jwtTokenUtils.parseClaims(accessToken).getSubject();

        // 헤더의 회원정보가 존재하는 회원정보인지 체크
        Optional<Member> optionalMember = memberRepository.findByMemberName(memberName);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 탈퇴한 회원인지 체크
        if (optionalMember.get().getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_MEMBER);
        }

        Optional<MemberGameInfo> optionalMemberGameInfo = memberGameInfoJpaRepository.findByMemberId(optionalMember.get().getId());

        // 이미 프로필이 설정되어 있는지 체크
        if (optionalMemberGameInfo.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
        }

        MemberProfileUpdateRequest memberProfileUpdateRequest = new MemberProfileUpdateRequest();
        MemberGameInfo updateInfo = memberProfileUpdateRequest.updateMemberGameInfo(optionalMemberGameInfo.get(), memberDto);
        memberGameInfoJpaRepository.save(updateInfo);

        return MemberProfileResponse.of(updateInfo, optionalMember.get().getId());
    }


    @Override
    public MemberProfileRead readProfile(String accessToken) {
        String memberName = jwtTokenUtils.parseClaims(accessToken).getSubject();

        // 헤더의 회원정보가 존재하는 회원정보인지 체크
        Optional<Member> optionalMember = memberRepository.findByMemberName(memberName);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 탈퇴한 회원인지 체크
        if (optionalMember.get().getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_MEMBER);
        }

        Optional<MemberGameInfo> optionalMemberGameInfo = memberGameInfoJpaRepository.findByMemberId(optionalMember.get().getId());

        // 이미 프로필이 설정되어 있는지 체크
        if (optionalMemberGameInfo.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_ERROR);
        }

        return MemberProfileRead.builder()
                .nickname(optionalMemberGameInfo.get().getNickname())
                .profileImg(optionalMemberGameInfo.get().getProfileImg())
                .level(optionalMemberGameInfo.get().getLevel().getName())
                .exp(optionalMemberGameInfo.get().getExp())
                // 최종레벨일 경우, nextLevel 비노출 필요
                .nextLevel(nextLevel(optionalMemberGameInfo.get().getLevel()).getName())
                .rank(rank(optionalMember.get().getId()))
                .build();
    }

    @Override
    public MemberProfileResponse levelUp(Long memberId) {

        // 해당 회원의 게임정보 호출
        Optional<MemberGameInfo> optionalMemberGameInfo = memberGameInfoJpaRepository.findByMemberId(memberId);
        if (optionalMemberGameInfo.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_GAME_INFO);
        }

        // 가장 최근 게임기록
        GameRecord gameRecord = gameRecordJpaRepository.findTOP1ByMemberIdOrderByIdDesc(memberId);

        // 현재 레벨
        Level level = optionalMemberGameInfo.get().getLevel();

        // 현재 경험치
        Long exp = optionalMemberGameInfo.get().getExp();

        // 레벨업까지 목표 경험치
        Long goalExp = level.getGoalExp();

        // 게임 속 내 역할
        String inGameRole = optionalMemberGameInfo.get().getInGameRole().getVal();

        // 이긴 팀(true: 마피아, false: 시민)
        boolean isWinner = gameRecord.isWinner();

        // 게임 속 내 역할이 없을 경우
        if (inGameRole.equals("none")) {
            throw new CustomException(ErrorCode.NO_IN_GAME_ROLE);
        }

        // 경험치 부여(내가 속한 팀이 이김: +200exp, 짐: +100exp)
        exp += ((isWinner && inGameRole.equals("mafia")
                || !isWinner && (inGameRole.equals("civilian")
                || inGameRole.equals("police")
                || inGameRole.equals("doctor"))
        )
                ? 200 : 100);

        // 레벨업이 가능할 경우
        if (!(level.equals(Level.BOSS) || level.equals(Level.NONE)) && (exp >= goalExp)) {
            level = nextLevel(level);
        }

        // 멤버 게임정보 업데이트
        MemberGameInfo updateLevelAndExp = MemberGameInfo.builder()
                .id(optionalMemberGameInfo.get().getId())
                .memberId(optionalMemberGameInfo.get().getMemberId())
                .nickname(optionalMemberGameInfo.get().getNickname())
                .profileImg(optionalMemberGameInfo.get().getProfileImg())
                .level(level)
                .exp(exp)
                .inGameRole(optionalMemberGameInfo.get().getInGameRole())
                .build();

        memberGameInfoJpaRepository.save(updateLevelAndExp);
        return MemberProfileResponse.of(updateLevelAndExp, memberId);
    }

    @Override
    public Long rank(Long memberId) {
        return memberGameInfoJpaRepository.findByMemberRank(memberId);
    }

    @Override
    public Level nextLevel(Level rawLevel) {
        if (rawLevel.equals(Level.BEGINNER)) {
            return Level.ASSOCIATE;
        } else if (rawLevel.equals(Level.ASSOCIATE)) {
            return Level.SOLDIER;
        } else if (rawLevel.equals(Level.SOLDIER)) {
            return Level.CAPTAIN;
        } else if (rawLevel.equals(Level.CAPTAIN)) {
            return Level.UNDERBOSS;
        } else if (rawLevel.equals(Level.UNDERBOSS)) {
            return Level.BOSS;
        } else
            return Level.NONE;
    }

    @Override
    public boolean memberExists(String memberName) {
        return memberRepository.existsByMemberName(memberName);
    }


    /**
     * 멤버 게임 기록 update
     */
    @Override
    public MemberRecordResponse readRecord(String accessToken) {
        String memberName = jwtTokenUtils.parseClaims(accessToken).getSubject();

        // 헤더의 회원정보가 존재하는 회원정보인지 체크
        Optional<Member> optionalMember = memberRepository.findByMemberName(memberName);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 탈퇴한 회원인지 체크
        if (optionalMember.get().getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_MEMBER);
        }

        Optional<MemberRecord> optionalMemberRecord = memberRecordJpaRepository.findByMemberId(optionalMember.get().getId());
        if (optionalMemberRecord.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER_RECORD);
        }

        return MemberRecordResponse.of(optionalMemberRecord.get());
    }


    /**
     * 멤버 게임 기록 update
     */
    @Override
    public MemberRecordResponse updateRecord(Long memberId) {

        // memberRecord(게임한 기록) 호출
        Optional<MemberRecord> optionalMemberRecord = memberRecordJpaRepository.findByMemberId(memberId);
        if (optionalMemberRecord.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER_RECORD);
        }

        // memberGameInfo(프로필 정보) 호출
        Optional<MemberGameInfo> optionalMemberGameInfo = memberGameInfoJpaRepository.findByMemberId(memberId);
        if (optionalMemberGameInfo.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_GAME_INFO);
        }

        // 내 게임 기록
        MemberRecord memberRecord = optionalMemberRecord.get();

        // 가장 최근에 한 gameRecord(게임 정보)
        GameRecord gameRecord = gameRecordJpaRepository.findTOP1ByMemberIdOrderByIdDesc(memberId);

        // 게임 속 내 역할
        String inGameRole = optionalMemberGameInfo.get().getInGameRole().getVal();

        // 역할별 횟수 업데이트
        Long mafiaCnt = updateInGameRoleCnt(inGameRole, memberRecord).get("mafia");
        Long citizenCnt = updateInGameRoleCnt(inGameRole, memberRecord).get("citizen");
        Long policeCnt = updateInGameRoleCnt(inGameRole, memberRecord).get("police");
        Long doctorCnt = updateInGameRoleCnt(inGameRole, memberRecord).get("doctor");

        // 역할별 이긴 횟수 업데이트
        Long mafiaWinCnt = updateWinCnt(inGameRole, memberRecord, gameRecord).get("mafiaWinCnt");
        Long citizenWinCnt = updateWinCnt(inGameRole, memberRecord, gameRecord).get("citizenWinCnt");
        Long policeWinCnt = updateWinCnt(inGameRole, memberRecord, gameRecord).get("policeWinCnt");
        Long doctorWinCnt = updateWinCnt(inGameRole, memberRecord, gameRecord).get("doctorWinCnt");

        // 총 게임 횟수 업데이트
        Long totalCnt = updateTotalCnt(memberRecord).get("totalCnt");

        // 총 게임 시간 업데이트(totalTime)
        Long totalTime = updateTotalTime(memberRecord, gameRecord).get("totalTime");

        // TODO 살린 횟수 업데이트(saveCnt)
        Long saveCnt = memberRecord.getSaveCnt();

        // TODO 죽인 횟수 업데이트(killCnt)
        Long killCnt = memberRecord.getKillCnt();

        MemberRecord updateMemberRecord = MemberRecord.builder()
                .id(memberRecord.getId())
                .memberId(memberId)
                .mafiaCnt(mafiaCnt)
                .citizenCnt(citizenCnt)
                .policeCnt(policeCnt)
                .doctorCnt(doctorCnt)
                .citizenWinCnt(citizenWinCnt)
                .mafiaWinCnt(mafiaWinCnt)
                .policeWinCnt(policeWinCnt)
                .doctorWinCnt(doctorWinCnt)
                .saveCnt(saveCnt)
                .killCnt(killCnt)
                .totalCnt(totalCnt)
                .totalTime(totalTime)
                .build();

        memberRecordJpaRepository.save(updateMemberRecord);
        return MemberRecordResponse.of(updateMemberRecord);
    }


    /**
     * 역할별 게임 횟수 update
     */
    @Override
    public Map<String, Long> updateInGameRoleCnt(String inGameRole, MemberRecord memberRecord) {
        Long mafiaCnt = memberRecord.getMafiaCnt();
        Long citizenCnt = memberRecord.getCitizenCnt();
        Long policeCnt = memberRecord.getPoliceCnt();
        Long doctorCnt = memberRecord.getDoctorCnt();

        switch (inGameRole) {
            case "mafia":
                mafiaCnt++;
                break;
            case "civilian":
                citizenCnt++;
                break;
            case "police":
                policeCnt++;
                break;
            case "doctor":
                doctorCnt++;
                break;
        }

        Map<String, Long> inGameRoleCntMap = new HashMap<>();

        inGameRoleCntMap.put("mafia", mafiaCnt);
        inGameRoleCntMap.put("citizen", citizenCnt);
        inGameRoleCntMap.put("police", policeCnt);
        inGameRoleCntMap.put("doctor", doctorCnt);

        return inGameRoleCntMap;
    }

    /**
     * 시민/마피아로 이긴 팀 횟수 update
     */
    @Override
    public Map<String, Long> updateWinCnt(String inGameRole, MemberRecord memberRecord, GameRecord gameRecord) {
        // 이긴 팀(true: 마피아, false: 시민)
        boolean isWinner = gameRecord.isWinner();

        Long mafiaWinCnt = memberRecord.getMafiaWinCnt();
        Long citizenWinCnt = memberRecord.getCitizenWinCnt();
        Long policeWinCnt = memberRecord.getPoliceWinCnt();
        Long doctorWinCnt = memberRecord.getDoctorWinCnt();

        if (isWinner && inGameRole.equals("mafia")) {
            mafiaWinCnt++;
        } else if (!isWinner && inGameRole.equals("civilian")) {
            citizenWinCnt++;
        } else if (!isWinner && inGameRole.equals("police")) {
            policeWinCnt++;
        } else if (!isWinner && inGameRole.equals("doctor")) {
            doctorWinCnt++;
        }

        Map<String, Long> winCntMap = new HashMap<>();
        winCntMap.put("mafiaWinCnt", mafiaWinCnt);
        winCntMap.put("citizenWinCnt", citizenWinCnt);
        winCntMap.put("policeWinCnt", policeWinCnt);
        winCntMap.put("doctorWinCnt", doctorWinCnt);

        return winCntMap;
    }

    /**
     * 총 게임 횟수 update
     */
    @Override
    public Map<String, Long> updateTotalCnt(MemberRecord memberRecord) {
        Long totalCnt = memberRecord.getTotalCnt();
        totalCnt++;

        Map<String, Long> totalCntMap = new HashMap<>();
        totalCntMap.put("totalCnt", totalCnt);
        return totalCntMap;
    }

    /**
     * 총 게임 시간 update
     */
    @Override
    public Map<String, Long> updateTotalTime(MemberRecord memberRecord, GameRecord gameRecord) {
        Long totalTime = memberRecord.getTotalTime();
        int duration = gameRecord.getDuration();
        totalTime += duration;

        Map<String, Long> totalTimeMap = new HashMap<>();
        totalTimeMap.put("totalTime", totalTime);

        return totalTimeMap;
    }

    /**
     * 멤버 기록 생성(초기화)
     * 회원가입 시 (signUp 메소드 내에서) 호출
     */
    @Override
    public MemberRecord newMemberRecord(String memberName) {
        Optional<Member> optionalMember = memberRepository.findByMemberName(memberName);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        return MemberRecord.builder()
                .memberId(optionalMember.get().getId())
                .mafiaCnt(0L)
                .citizenCnt(0L)
                .doctorCnt(0L)
                .policeCnt(0L)
                .saveCnt(0L)
                .killCnt(0L)
                .mafiaWinCnt(0L)
                .citizenWinCnt(0L)
                .policeWinCnt(0L)
                .doctorWinCnt(0L)
                .totalCnt(0L)
                .totalTime(0L)
                .build();
    }
}

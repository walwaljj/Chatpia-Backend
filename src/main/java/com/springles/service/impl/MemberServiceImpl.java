package com.springles.service.impl;


import com.springles.domain.dto.member.*;
import com.springles.domain.entity.BlackListToken;
import com.springles.domain.entity.Member;
import com.springles.domain.entity.RefreshToken;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.jwt.JwtTokenUtils;
import com.springles.repository.BlackListTokenRedisRepository;
import com.springles.repository.MemberJpaRepository;
import com.springles.repository.RefreshTokenRedisRepository;
import com.springles.service.MemberService;
import com.sun.mail.smtp.SMTPAddressFailedException;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberJpaRepository memberRepository;
    private final RefreshTokenRedisRepository memberRedisRepository;
    private final BlackListTokenRedisRepository blackListTokenRedisRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final JavaMailSender javaMailSender;

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

        memberRepository.save(memberDto.newMember(passwordEncoder));
        return MemberCreateRequest.fromEntity(memberDto.newMember(passwordEncoder)).toString();
    }

    @Override
    public String updateInfo(MemberUpdateRequest memberDto, String authHeader) {

        String memberName = jwtTokenUtils.parseClaims(authHeader.split(" ")[1]).getSubject();

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
    public void signOut(MemberDeleteRequest memberDto, String authHeader) {

        String memberName = jwtTokenUtils.parseClaims(authHeader.split(" ")[1]).getSubject();

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
    public String login(MemberLoginRequest memberDto) {
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
        if(optionalRefreshToken.isPresent()) {
            refreshTokenRedisRepository.deleteById(optionalRefreshToken.get().getId());
        }

        // accessToken 생성
        String accessToken = jwtTokenUtils.generatedToken(memberDto.getMemberName());

        // refreshToken 생성
        RefreshToken refreshToken = jwtTokenUtils.generaedRefreshToken(memberDto.getMemberName());

        // refreshToken 저장
        memberRedisRepository.save(refreshToken);

        return MemberLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberName(memberDto.getMemberName())
                .build()
                .toString();
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
        if(optionalRefreshToken.isEmpty()) {
            throw new CustomException(ErrorCode.NO_JWT_TOKEN);
        }
        refreshTokenRedisRepository.deleteById(optionalRefreshToken.get().getId());

        // 블랙리스트에 저장
        BlackListToken blackListToken = BlackListToken.builder()
                .accessToken(authHeader.split(" ")[1])
                // accessToken의 남은 유효시간만큼만 저장
                .expiration((rawExpiration.getTime() - Date.from(Instant.now()).getTime())/1000)
                .build();
        log.info("token Expiration : " + rawExpiration);

        blackListTokenRedisRepository.save(blackListToken);
    }


    @Override
    public String vertificationId(MemberVertifIdRequest memberDto) {

        List<Member> memberList = memberRepository.findAllByEmail(memberDto.getEmail());
        if(memberList.isEmpty()) {
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
        if(optionalMember.isEmpty()) {
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

        for(int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * charSet.length);
            tempPassword += charSet[index];
        }

        return tempPassword;
    }

    @Override
    public boolean memberExists(String memberName) {
        return memberRepository.existsByMemberName(memberName);
    }
}

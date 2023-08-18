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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
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

//        // 기존에 생성된 토큰 정보가 있을 경우 삭제(데이터 낭비 방지)
//        Optional<RefreshToken> optionalRefreshToken = memberRedisRepository.findByMemberName(optionalMember.get().getMemberName());
//        if (optionalRefreshToken.isPresent()) {
//            memberRedisRepository.deleteByMemberName(optionalMember.get().getMemberName());
//        }

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
    public boolean memberExists(String memberName) {
        return memberRepository.existsByMemberName(memberName);
    }
}

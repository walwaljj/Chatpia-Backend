package com.springles.service.impl;


import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.dto.member.MemberDeleteRequest;
import com.springles.domain.dto.member.MemberUpdateRequest;
import com.springles.domain.entity.Member;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.repository.MemberJpaRepository;
import com.springles.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberJpaRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
        return memberDto.newMember(passwordEncoder).toString();
    }

    @Override
    public String updateInfo(MemberUpdateRequest memberDto, Long memberId) {

        Optional<Member> optionalMember = memberRepository.findById(memberId);
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
    public void signOut(MemberDeleteRequest memberDto, Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
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

        memberRepository.deleteById(memberId);
    }

    public boolean memberExists(String memberName) {
        return memberRepository.existsByMemberName(memberName);
    }
}

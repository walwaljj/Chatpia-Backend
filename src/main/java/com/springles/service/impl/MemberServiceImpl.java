package com.springles.service.impl;


import com.springles.domain.dto.member.MemberCreateResponse;
import com.springles.domain.dto.member.MemberUpdateResponse;
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
    public String signUp(MemberCreateResponse member) {

        // 아이디 기 사용 여부 체크
        if (memberExists(member.getMemberName())) {
            throw new CustomException(ErrorCode.EXIST_MEMBERNAME);
        }

        // 비밀번호와 비밀번호 확인 값 일치 여부 체크
        if (!member.getPassword().equals(member.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        memberRepository.save(member.newMember(passwordEncoder));
        return member.newMember(passwordEncoder).toString();
    }

    @Override
    public String updateInfo(MemberUpdateResponse member, Long memberId) {

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
            if (!updateMember.getEmail().equals(member.getEmail())) {
                updateMember.setEmail(member.getEmail());
            }
        } catch (NullPointerException e) {
            updateMember.setEmail(member.getEmail());
        }

        // 비밀번호와 비밀번호 확인 값 일치여부 체크
        if (!member.getPassword().equals(member.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        updateMember.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepository.save(updateMember);

        return updateMember.toString();
    }

    @Override
    public String signOut(MemberCreateResponse member, Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 입력한 비밀번호와 기존 비밀번호 일치 여부 체크
        if (!(passwordEncoder.matches(member.getPassword(), optionalMember.get().getPassword()))) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        memberRepository.deleteById(memberId);

        return optionalMember.get().getMemberName() + " 회원 탈퇴 완료";
    }

    public boolean memberExists(String memberName) {
        return memberRepository.existsByMemberName(memberName);
    }
}

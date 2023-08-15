package com.springles.service.impl;


import com.springles.domain.dto.member.MemberResponse;
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
    public String signUp(MemberResponse member) {

        // 아이디 입력 여부 체크
        if (member.getMemberName() == null) {
            throw new CustomException(ErrorCode.NULL_MEMBERNAME);
        }

        // 아이디 글자 수 유효성 체크 (6글자 이상 20자 이하)
        if (!(6 <= member.getMemberName().length() && member.getMemberName().length() <= 20)) {
            throw new CustomException(ErrorCode.OUT_OF_CHARACTER_LIMIT_MEMBERNAME);
        }

        // 아이디 입력값 조건 유효성 체크 (영문 대소문자, 숫자만 입력 가능)
        if (!member.getMemberName().matches("^[a-zA-Z0-9]+$")) {
            throw new CustomException((ErrorCode.INVALID_MEMBERNAME));
        }

        // 아이디 기 사용 여부 체크
        if (memberExists(member.getMemberName())) {
            throw new CustomException(ErrorCode.EXIST_MEMBERNAME);
        }

        // 비밀번호 입력 여부 체크
        if (member.getPassword() == null) {
            throw new CustomException(ErrorCode.NULL_PASSWORD);
        }

        // 비밀번호 글자 수 유효성 체크 (6자 이상)
        if (!(6 <= member.getPassword().length())) {
            throw new CustomException(ErrorCode.OUT_OF_CHARACTER_LIMIT_PASSWORD);
        }

        // 비밀번호 입력값 조건 유효성 체크 (영문 대소문자, 숫자, 특수문자(!@#$%^&*))
        if (!member.getPassword().matches("^[a-zA-Z0-9!@#$%^&*]+$")) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 비밀번호와 비밀번호 확인 값 일치 여부 체크
        if (!member.getPassword().equals(member.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        // 이메일 입력여부 체크
        if (member.getEmail() != null) {
            // 이메일 형식 유효성 체크
            if (!member.getEmail().matches("^[a-zA-Z0-9\\._%+-]+@[a-zA-Z0-9\\.-]+\\.[a-zA-Z]{2,}$")) {
                throw new CustomException(ErrorCode.INVALID_EMAIL);
            }
        }

        memberRepository.save(member.newMember(passwordEncoder));
        return member.newMember(passwordEncoder).toString();
    }

    @Override
    public String updateInfo(MemberResponse memberDetails, Long memberId) {

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
            // 이메일이 변경되었는지 체크
            // 기존 이메일이 null이 아니면서
            if (!updateMember.getEmail().equals(memberDetails.getEmail())) {
                // 변경하고자 하는 이메일 값이 null이 아닌 경우
                if (memberDetails.getEmail() != null) {
                    // 이메일 형식 유효성 체크 후 이메일 변경
                    if (!memberDetails.getEmail().matches("^[a-zA-Z0-9\\._%+-]+@[a-zA-Z0-9\\.-]+\\.[a-zA-Z]{2,}$")) {
                        throw new CustomException(ErrorCode.INVALID_EMAIL);
                    }
                    updateMember.setEmail(memberDetails.getEmail());
                } else {
                    // 변경하고자 하는 이메일 값이 null인 경우, 별도 체크 없이 이메일 변경
                    updateMember.setEmail(memberDetails.getEmail());
                }
            }
        } catch (NullPointerException e) {
            // 기존 이메일이 null이면서
            // 변경하고자 하는 이메일 값이 null이 아닌 경우
            if (memberDetails.getEmail() != null) {
                // 이메일 형식 유효성 체크 후 이메일 변경
                if (!memberDetails.getEmail().matches("^[a-zA-Z0-9\\._%+-]+@[a-zA-Z0-9\\.-]+\\.[a-zA-Z]{2,}$")) {
                    throw new CustomException(ErrorCode.INVALID_EMAIL);
                }
                updateMember.setEmail(memberDetails.getEmail());
            }
            // 변경하고자 하는 이메일 값이 null이면 변경 없음
        }

        // 비밀번호 입력 여부 체크
        if (updateMember.getPassword() == null) {
            throw new CustomException(ErrorCode.NULL_PASSWORD);
        }

        // 비밀번호 글자 수 유효성 체크 (6자 이상)
        if (!(6 <= updateMember.getPassword().length())) {
            throw new CustomException(ErrorCode.OUT_OF_CHARACTER_LIMIT_PASSWORD);
        }

        // 비밀번호 입력값 조건 유효성 체크 (영문 대소문자, 숫자, 특수문자(!@#$%^&*))
        if (!memberDetails.getPassword().matches("^[a-zA-Z0-9!@#$%^&*]+$")) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 비밀번호와 비밀번호 확인 값 일치여부 체크
        if (!memberDetails.getPassword().equals(memberDetails.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        updateMember.setPassword(passwordEncoder.encode(memberDetails.getPassword()));
        memberRepository.save(updateMember);

        return updateMember.toString();
    }

    @Override
    public String signOut(MemberResponse memberDetails, Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 비밀번호 입력 여부 체크
        if (memberDetails.getPassword() == null) {
            throw new CustomException(ErrorCode.NULL_PASSWORD);
        }

        // 입력한 비밀번호와 기존 비밀번호 일치 여부 체크
        if (!(passwordEncoder.matches(memberDetails.getPassword(), optionalMember.get().getPassword()))) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        Member deleteMember = optionalMember.get();
        deleteMember.setIsDeleted(true);
        memberRepository.save(deleteMember);

        return deleteMember.toString();
    }

    public boolean memberExists(String memberName) {
        return memberRepository.existsByMemberName(memberName);
    }
}

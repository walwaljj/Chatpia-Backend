package com.springles.repository.impl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.springles.domain.entity.Member;
import com.springles.domain.entity.QMember;
import com.springles.repository.custom.MemberCustomRepository;
import com.springles.repository.support.Querydsl4RepositorySupport;
import io.micrometer.common.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.springles.domain.entity.QMember.member;

public class MemberRepositoryImpl extends Querydsl4RepositorySupport implements MemberCustomRepository {
    public MemberRepositoryImpl() {
        super(Member.class);
    }
    @Override
    public Optional<List<Member>> findAllByMemberNameContainingIgnoreCase(String nickname) {
        List<Member> memberList = selectFrom(member)
                .where(member.memberName.eq(nickname))
                .fetch();
        return Optional.ofNullable(memberList);
    }

    public static Predicate searchMemberNameContains(String nameContains) {
        BooleanBuilder builder = new BooleanBuilder();
        QMember qmember = member;

        builder.and(qmember.memberName.eq(nameContains));

        if (StringUtils.isNotEmpty(nameContains)) {
            builder = builder.and(qmember.memberName.containsIgnoreCase(nameContains));
        }

        return builder;
    }
}

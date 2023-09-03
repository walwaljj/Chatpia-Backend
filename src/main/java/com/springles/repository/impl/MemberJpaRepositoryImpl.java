package com.springles.repository.impl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.springles.domain.entity.Member;
import com.springles.domain.entity.QMember;
import com.springles.repository.custom.MemberJpaRepositoryCustom;
import com.springles.repository.support.Querydsl4RepositorySupport;
import io.micrometer.common.util.StringUtils;

import java.util.List;

import static com.springles.domain.entity.QMember.member;

public class MemberJpaRepositoryImpl extends Querydsl4RepositorySupport implements
    MemberJpaRepositoryCustom {
    public MemberJpaRepositoryImpl() {
        super(Member.class);
    }

    @Override
    public List<Member> findAllByMemberNameContainingIgnoreCase(String nickname) {
        return selectFrom(member)
            .where(member.memberName.eq(nickname))
            .fetch();
    }

    public static Predicate searchMemberNameContains(String nameContains) {
        BooleanBuilder builder = new BooleanBuilder();
        QMember qmember = member;

        builder.and(qmember.memberName.eq(nameContains));

        if (StringUtils.isNotEmpty(nameContains)) {
            builder.and(qmember.memberName.containsIgnoreCase(nameContains));
        }

        return builder;
    }
}

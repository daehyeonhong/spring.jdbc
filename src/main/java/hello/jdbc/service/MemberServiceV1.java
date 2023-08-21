package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;

import java.sql.SQLException;

public class MemberServiceV1 {
    private final MemberRepositoryV1 memberRepository;

    public MemberServiceV1(final MemberRepositoryV1 memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(final String fromId, final String toId, final int money) throws SQLException {
        final Member fromMember = this.memberRepository.findById(fromId);
        final Member toMember = this.memberRepository.findById(toId);

        this.memberRepository.update(fromId, fromMember.getMoney() - money);

        validation(toMember);

        this.memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(final Member toMember) {
        if (toMember.getMemberId().equals("ex")) throw new IllegalArgumentException("이체중 예외 발생");
    }
}

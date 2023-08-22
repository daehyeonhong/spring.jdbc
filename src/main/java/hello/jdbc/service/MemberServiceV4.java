package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예외 누수 문제 해결
 * throws SQLException 제거
 */
public class MemberServiceV4 {
    private final MemberRepository memberRepository;
    private final static Logger log = LoggerFactory.getLogger(MemberServiceV4.class);

    public MemberServiceV4(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(final String fromId, final String toId, final int money) {
        this.accountTransferBisunessLogic(fromId, toId, money);
    }

    private void accountTransferBisunessLogic(final String fromId, final String toId, final int money) {
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

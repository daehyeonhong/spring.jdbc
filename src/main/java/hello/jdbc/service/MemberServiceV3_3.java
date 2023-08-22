package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * `Transaction` - `Transactional AOP`
 */
public class MemberServiceV3_3 {
    private final MemberRepositoryV3 memberRepository;
    private final static Logger log = LoggerFactory.getLogger(MemberServiceV3_3.class);

    public MemberServiceV3_3(final MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(final String fromId, final String toId, final int money) throws SQLException {
        this.accountTransferBisunessLogic(fromId, toId, money);
    }

    private void accountTransferBisunessLogic(final String fromId, final String toId, final int money) throws SQLException {
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

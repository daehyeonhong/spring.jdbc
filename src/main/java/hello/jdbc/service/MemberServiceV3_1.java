package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * `Transaction` - `Parameterization`, `ConnectionPool`
 */
public class MemberServiceV3_1 {
    private final PlatformTransactionManager platformTransactionManager;
    private final MemberRepositoryV3 memberRepository;
    private final static Logger log = LoggerFactory.getLogger(MemberServiceV3_1.class);

    public MemberServiceV3_1(final PlatformTransactionManager platformTransactionManager, final MemberRepositoryV3 memberRepository) {
        this.platformTransactionManager = platformTransactionManager;
        this.memberRepository = memberRepository;
    }


    public void accountTransfer(final String fromId, final String toId, final int money) throws SQLException {
        final TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            accountTransferBisunessLogic(fromId, toId, money);
            platformTransactionManager.commit(status);
        } catch (final Exception e) {
            platformTransactionManager.rollback(status);
            throw new IllegalArgumentException(e);
        }
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

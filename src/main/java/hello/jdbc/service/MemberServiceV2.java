package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * `Transaction` - `Parameterization`, `ConnectionPool`
 */
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;
    private final static Logger log = LoggerFactory.getLogger(MemberServiceV2.class);

    public MemberServiceV2(final DataSource dataSource, final MemberRepositoryV2 memberRepository) {
        this.dataSource = dataSource;
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(final String fromId, final String toId, final int money) throws SQLException {
        final Connection connection = this.dataSource.getConnection();
        try {
            connection.setAutoCommit(false);

            accountTransferBisunessLogic(connection, fromId, toId, money);

            connection.commit();
        } catch (final Exception e) {
            connection.rollback();
            throw new IllegalArgumentException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // `ConnectionPool`을 고려하여, `Connection`을 `close`하기 전에 `AutoCommit`을 `true`로 설정해야 한다.
                    connection.close();
                } catch (final Exception exception) {
                    log.error("connection close error", exception);
                }
            }
        }
    }

    private void accountTransferBisunessLogic(final Connection connection, final String fromId, final String toId, final int money) throws SQLException {
        final Member fromMember = this.memberRepository.findById(connection, fromId);
        final Member toMember = this.memberRepository.findById(connection, toId);

        this.memberRepository.update(connection, fromId, fromMember.getMoney() - money);

        validation(toMember);

        this.memberRepository.update(connection, toId, toMember.getMoney() + money);
    }

    private static void validation(final Member toMember) {
        if (toMember.getMemberId().equals("ex")) throw new IllegalArgumentException("이체중 예외 발생");
    }
}

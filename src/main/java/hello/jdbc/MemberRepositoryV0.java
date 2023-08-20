package hello.jdbc;

import hello.jdbc.connection.DatabaseConnectionUtil;
import hello.jdbc.domain.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
public class MemberRepositoryV0 {
    private static final Logger log = LoggerFactory.getLogger(MemberRepositoryV0.class);

    public Member save(final Member member) throws SQLException {
        final String sql = "INSERT INTO MEMBER(MEMBER_ID, MONEY) VALUES (?, ?)";
        log.info("sql: {}", sql);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseConnectionUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            final int affectedRows = preparedStatement.executeUpdate();
            log.info("affectedRows: {}", affectedRows);
            return member;
        } catch (SQLException sqlException) {
            log.error("Database Error", sqlException);
            throw new SQLException(sqlException);
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private static void close(final Connection connection, final Statement statement, final ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException sqlException) {
                log.error("Database Error", sqlException);
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException sqlException) {
                log.error("Database Error", sqlException);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException sqlException) {
                log.error("Database Error", sqlException);
            }
        }
    }
}

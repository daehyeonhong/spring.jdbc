package hello.jdbc;

import hello.jdbc.connection.DatabaseConnectionUtil;
import hello.jdbc.domain.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.NoSuchElementException;

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
            throw sqlException;
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    public Member findById(final String memberId) throws SQLException {
        final String sql = "SELECT MEMBER_ID, MONEY FROM MEMBER WHERE MEMBER_ID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnectionUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final String memberIdFromDatabase = resultSet.getString("MEMBER_ID");
                final int money = resultSet.getInt("MONEY");
                final Member member = new Member(memberIdFromDatabase, money);
                log.info("member: {}", member);
                return member;
            }
            throw new NoSuchElementException("member not found memberId: " + memberId);
        } catch (SQLException sqlException) {
            log.error("Database Error", sqlException);
            throw sqlException;
        } finally {
            close(connection, preparedStatement, resultSet);
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

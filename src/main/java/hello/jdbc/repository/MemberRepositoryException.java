package hello.jdbc.repository;

import hello.jdbc.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryException {
    Member save(final Member member) throws SQLException;

    Member findById(final String memberId) throws SQLException;

    void update(final String memberId, final int money) throws SQLException;

    void delete(final String memberId) throws SQLException;

    void deleteAll();
}

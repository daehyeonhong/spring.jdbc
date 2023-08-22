package hello.jdbc.repository;

import hello.jdbc.domain.Member;

public interface MemberRepository {
    Member save(final Member member);

    Member findById(final String memberId);

    void update(final String memberId, final int money);

    void delete(final String memberId);

    void deleteAll();
}

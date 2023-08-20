package hello.jdbc;

import hello.jdbc.domain.Member;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberRepositoryV0Test {
    private final MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        final Member member = new Member("memberId", 10_000);
        final Member savedMember =this. memberRepositoryV0.save(member);
        assertEquals(member, savedMember);
    }

}

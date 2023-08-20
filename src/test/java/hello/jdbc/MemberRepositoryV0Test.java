package hello.jdbc;

import hello.jdbc.domain.Member;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRepositoryV0Test {
    private final static Logger log = LoggerFactory.getLogger(MemberRepositoryV0Test.class);
    private final MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        // save
        final Member member = new Member("memberId", 10_000);
        final Member savedMember = this.memberRepositoryV0.save(member);
        assertThat(savedMember).isEqualTo(member);

        // findById
        final Member foundMember = this.memberRepositoryV0.findById(member.getMemberId());
        log.info("foundMember: {}", foundMember);
        assertThat(foundMember).isEqualTo(member);
    }
}

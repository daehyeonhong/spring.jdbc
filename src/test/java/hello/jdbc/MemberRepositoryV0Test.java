package hello.jdbc;

import hello.jdbc.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberRepositoryV0Test {
    private final static Logger log = LoggerFactory.getLogger(MemberRepositoryV0Test.class);
    private final MemberRepositoryV0 memberRepository = new MemberRepositoryV0();

    @AfterEach
    void tearDown() {
        this.memberRepository.deleteAll();
    }

    @Test
    void crud() throws SQLException {
        // save
        final Member member = new Member("memberId", 10_000);
        final Member savedMember = this.memberRepository.save(member);
        assertThat(savedMember).isEqualTo(member);

        // findById
        final Member foundMember = this.memberRepository.findById(member.getMemberId());
        log.info("foundMember: {}", foundMember);
        assertThat(foundMember).isEqualTo(member);

        // update: money: 10_000 -> 20_000
        this.memberRepository.update(member.getMemberId(), 20_000);
        final Member updatedMember = this.memberRepository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20_000);

        // delete
        this.memberRepository.delete(member.getMemberId());
        assertThatThrownBy(() -> this.memberRepository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }
}

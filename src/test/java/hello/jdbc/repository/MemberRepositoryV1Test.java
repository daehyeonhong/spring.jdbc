package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberRepositoryV1Test {
    private final static Logger log = LoggerFactory.getLogger(MemberRepositoryV1Test.class);
    private MemberRepositoryV1 memberRepository;

    @AfterEach
    void tearDown() {
        this.memberRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        // Default: DriverManager - 항상 새로운 `Connection`을 생성한다.
//        final DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // HikariCP - `ConnectionPooling`
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        memberRepository = new MemberRepositoryV1(dataSource);
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

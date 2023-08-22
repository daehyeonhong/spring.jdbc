package hello.jdbc.service;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Transaction - TransactionManager
 */
@SpringBootTest
class MemberServiceV3_3Test {
    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";
    private static final int MONEY = 10_000;
    public static final int TRANSFER_AMOUNT = 2_000;
    private final static Logger log = LoggerFactory.getLogger(MemberServiceV3_3Test.class);
    @Autowired
    private MemberRepositoryV3 memberRepository;
    @Autowired
    private MemberServiceV3_3 memberService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            final HikariDataSource hikariDataSource = new HikariDataSource();
            hikariDataSource.setJdbcUrl(URL);
            hikariDataSource.setUsername(USERNAME);
            hikariDataSource.setPassword(PASSWORD);
            return hikariDataSource;
        }

        @Bean
        PlatformTransactionManager platformTransactionManager() {
            return new DataSourceTransactionManager(this.dataSource());
        }

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(this.dataSource());
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(this.memberRepositoryV3());
        }
    }

    @AfterEach
    void tearDown() {
        this.memberRepository.deleteAll();
    }

    @Test
    void AopCheck() {
        log.info("memberService: {}", this.memberService.getClass());
        log.info("memberRepository: {}", this.memberRepository.getClass());
        assertThat(AopUtils.isAopProxy(this.memberService)).isTrue();
        assertThat(AopUtils.isAopProxy(this.memberRepository)).isFalse();
    }

    @Test
    @DisplayName(value = "정상 이체")
    void accountTransfer() throws SQLException {
        // given
        final Member memberA = new Member(MEMBER_A, MONEY);
        final Member memberB = new Member(MEMBER_B, MONEY);
        this.memberRepository.save(memberA);
        this.memberRepository.save(memberB);

        // when
        this.memberService.accountTransfer(MEMBER_A, MEMBER_B, TRANSFER_AMOUNT);

        // then
        final Member foundMemberA = this.memberRepository.findById(MEMBER_A);
        final Member foundMemberB = this.memberRepository.findById(MEMBER_B);

        assertThat(foundMemberA.getMoney()).isEqualTo(MONEY - TRANSFER_AMOUNT);
        assertThat(foundMemberB.getMoney()).isEqualTo(MONEY + TRANSFER_AMOUNT);
    }

    @Test
    @DisplayName(value = "이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        final Member memberA = new Member(MEMBER_A, MONEY);
        final Member memberEx = new Member(MEMBER_EX, MONEY);
        this.memberRepository.save(memberA);
        this.memberRepository.save(memberEx);

        // when
        assertThatThrownBy(() -> this.memberService.accountTransfer(MEMBER_A, MEMBER_EX, TRANSFER_AMOUNT)).isInstanceOf(IllegalArgumentException.class);

        // then
        final Member foundMemberA = this.memberRepository.findById(MEMBER_A);
        final Member foundMemberEx = this.memberRepository.findById(MEMBER_EX);

        assertThat(foundMemberA.getMoney()).isEqualTo(MONEY);
        assertThat(foundMemberEx.getMoney()).isEqualTo(MONEY);
    }

}

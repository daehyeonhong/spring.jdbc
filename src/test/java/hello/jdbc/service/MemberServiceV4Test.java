package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV4_2;
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

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Transaction - DataSource, TransactionManager AutoWired
 */
@SpringBootTest
class MemberServiceV4Test {
    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";
    private static final int MONEY = 10_000;
    public static final int TRANSFER_AMOUNT = 2_000;
    private final static Logger log = LoggerFactory.getLogger(MemberServiceV4Test.class);
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberServiceV4 memberService;

    @TestConfiguration
    static class TestConfig {
        private final DataSource dataSource;

        TestConfig(final DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepository memberRepository() {
            return new MemberRepositoryV4_2(this.dataSource);
        }

        @Bean
        MemberServiceV4 memberServiceV4() {
            return new MemberServiceV4(this.memberRepository());
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

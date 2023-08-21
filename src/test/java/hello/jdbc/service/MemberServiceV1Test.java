package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 기본 동작, `Transaction`이 없어서 문제 발생
 */
class MemberServiceV1Test {
    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";
    private static final int MONEY = 10_000;
    public static final int TRANSFER_AMOUNT = 2_000;

    private MemberRepositoryV1 memberRepository;
    private MemberServiceV1 memberService;

    @BeforeEach
    void setUp() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        this.memberRepository = new MemberRepositoryV1(dataSource);
        this.memberService = new MemberServiceV1(this.memberRepository);
    }

    @AfterEach
    void tearDown() {
        this.memberRepository.deleteAll();
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

        assertThat(foundMemberA.getMoney()).isEqualTo(MONEY - TRANSFER_AMOUNT);
        assertThat(foundMemberEx.getMoney()).isEqualTo(MONEY);
    }

}

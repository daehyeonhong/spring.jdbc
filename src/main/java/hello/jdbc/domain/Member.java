package hello.jdbc.domain;

import java.util.Objects;
import java.util.StringJoiner;

public class Member {
    private String memberId;
    private int money;

    public Member() {
    }

    public Member(final String memberId, final int money) {
        this.memberId = memberId;
        this.money = money;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(final String memberId) {
        this.memberId = memberId;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(final int money) {
        this.money = money;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final Member member = (Member) object;
        return money == member.money && Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, money);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Member.class.getSimpleName() + "[", "]")
                .add("memberId='" + memberId + "'")
                .add("money=" + money)
                .toString();
    }
}

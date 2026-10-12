package ricardocorreia.personal.tinyledger.domain.model;

import org.assertj.core.api.ThrowableAssert;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AmountIsNotPositiveException;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountTest {

    @Test
    void should_add_amount_when_is_positive() {

        // given
        Account account = new Account("Test Account");
        account.deposit(BigDecimal.TEN);

        // when
        BigDecimal result = account.deposit(BigDecimal.ONE);

        // then
        assertThat(result)
                .isEqualTo(BigDecimal.valueOf(11))
                .isEqualTo(account.getBalance());
    }

    @Test
    void should_throw_exception_when_depositing_negative_amount() {

        // given
        Account account = new Account("Test Account");

        // when
        ThrowableAssert.ThrowingCallable callable = () -> account.deposit(BigDecimal.ONE.negate());

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(AmountIsNotPositiveException.class)
                .hasMessage("Input amount needs to be positive, instead got [-1]");
    }

    @Test
    void should_withdraw_when_has_enough_balance() {

        // given
        Account account = new Account("Test Account");
        account.deposit(BigDecimal.TEN);

        // when
        BigDecimal result = account.withdraw(BigDecimal.ONE);

        // then
        assertThat(result)
                .isEqualTo(BigDecimal.valueOf(9))
                .isEqualTo(account.getBalance());
    }

    @Test
    void should_throw_exception_when_withdrawing_negative_amount() {

        // given
        Account account = new Account("Test Account");

        // when
        ThrowableAssert.ThrowingCallable callable = () -> account.withdraw(BigDecimal.ONE.negate());

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(AmountIsNotPositiveException.class)
                .hasMessage("Input amount needs to be positive, instead got [-1]");
    }

    @Test
    void should_throw_exception_when_withdrawing_but_not_enough_balance() {

        // given
        Account account = new Account("Test Account");
        account.deposit(BigDecimal.ONE);

        // when
        ThrowableAssert.ThrowingCallable callable = () -> account.withdraw(BigDecimal.TEN);

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage(
                        "Account with ID [%s] does not have enough balance. Current: [1], requested: [10], difference: [9]"
                                .formatted(account.getId()));
    }

    @Test
    void should_return_same_hash_code_as_id() {

        // given
        Account account = new Account("Test Account");

        // when
        int result = account.hashCode();

        // then
        assertThat(result)
                .isEqualTo(account.getId().hashCode());
    }

    @Test
    void should_be_equal_to_other_account_with_same_id() {

        // given
        Account account1 = new Account("Test Account");
        Account account2 = Instancio.of(Account.class)
                .set(field(Account::getId), account1.getId())
                .create();

        // when
        boolean result = account1.equals(account2);

        // then
        assertTrue(result);
    }

    @Test
    void should_not_equal_to_other_account_with_different_id() {

        // given
        Account account1 = new Account("Test Account");
        Account account2 = new Account("Test Account 2");

        // when
        boolean result = account1.equals(account2);

        // then
        assertFalse(result);
    }

    @Test
    void should_not_equal_to_object_that_is_not_account_type() {

        // given
        Account account = new Account("Test Account");
        Object other = new ArrayList<>();

        // when
        boolean result = account.equals(other);

        // then
        assertFalse(result);
    }
}
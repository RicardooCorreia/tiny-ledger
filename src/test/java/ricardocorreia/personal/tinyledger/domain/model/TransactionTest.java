package ricardocorreia.personal.tinyledger.domain.model;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.TransferToTheSameAccountException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.*;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Deposit;

class TransactionTest {

    @Nested
    class DepositTest {

        @Test
        void should_return_true_when_does_relate_to_account() {

            // given
            UUID accountId = UUID.randomUUID();
            Deposit relatedDeposit = new Deposit(UUID.randomUUID(), BigDecimal.ONE, Instant.now(), accountId);
            Deposit unrelatedDeposit = new Deposit(UUID.randomUUID(), BigDecimal.ONE, Instant.now(), UUID.randomUUID());

            // when
            boolean relatedResult = relatedDeposit.relatesTo(accountId);
            boolean unrelatedResult = unrelatedDeposit.relatesTo(accountId);

            // then
            assertTrue(relatedResult);
            assertFalse(unrelatedResult);
        }

        @Test
        void should_return_same_hash_code_as_id() {

            // given
            UUID id = UUID.randomUUID();
            Deposit deposit = new Deposit(id, BigDecimal.ONE, Instant.now(), UUID.randomUUID());

            // when
            int result = deposit.hashCode();

            // then
            assertThat(result)
                    .isEqualTo(id.hashCode());
        }

        @Test
        void should_be_equals_to_other_deposit_that_has_the_same_id() {

            // given
            UUID id = UUID.randomUUID();
            Deposit deposit1 = new Deposit(id, BigDecimal.ONE, Instant.now(), UUID.randomUUID());
            Deposit deposit2 = new Deposit(id, BigDecimal.TWO, Instant.now().plusSeconds(2), UUID.randomUUID());

            // when
            boolean result = deposit1.equals(deposit2);

            // then
            assertTrue(result);
        }

        @Test
        void should_not_be_equals_to_other_other_transaction_type() {

            // given
            UUID id = UUID.randomUUID();
            Instant instant = Instant.now();
            UUID accountId = UUID.randomUUID();
            Deposit deposit = new Deposit(id, BigDecimal.ONE, instant, accountId);
            Withdrawal withdrawal = new Withdrawal(id, BigDecimal.ONE, instant, accountId);

            // when
            boolean result = deposit.equals(withdrawal);

            // then
            assertFalse(result);
        }
    }

    @Nested
    class WithdrawalTest {

        @Test
        void should_return_true_when_does_relate_to_account() {

            // given
            UUID accountId = UUID.randomUUID();
            Withdrawal relatedWithdrawal = new Withdrawal(UUID.randomUUID(), BigDecimal.ONE, Instant.now(), accountId);
            Withdrawal unrelatedWithdrawal = new Withdrawal(UUID.randomUUID(), BigDecimal.ONE, Instant.now(), UUID.randomUUID());

            // when
            boolean relatedResult = relatedWithdrawal.relatesTo(accountId);
            boolean unrelatedResult = unrelatedWithdrawal.relatesTo(accountId);

            // then
            assertTrue(relatedResult);
            assertFalse(unrelatedResult);
        }

        @Test
        void should_return_same_hash_code_as_id() {

            // given
            UUID id = UUID.randomUUID();
            Withdrawal withdrawal = new Withdrawal(id, BigDecimal.ONE, Instant.now(), UUID.randomUUID());

            // when
            int result = withdrawal.hashCode();

            // then
            assertThat(result)
                    .isEqualTo(id.hashCode());
        }

        @Test
        void should_be_equals_to_other_deposit_that_has_the_same_id() {

            // given
            UUID id = UUID.randomUUID();
            Withdrawal withdrawal1 = new Withdrawal(id, BigDecimal.ONE, Instant.now(), UUID.randomUUID());
            Withdrawal withdrawal2 = new Withdrawal(id, BigDecimal.TWO, Instant.now().plusSeconds(2), UUID.randomUUID());

            // when
            boolean result = withdrawal1.equals(withdrawal2);

            // then
            assertTrue(result);
        }

        @Test
        void should_not_be_equals_to_other_other_transaction_type() {

            // given
            UUID id = UUID.randomUUID();
            Instant instant = Instant.now();
            UUID accountId = UUID.randomUUID();
            Withdrawal withdrawal = new Withdrawal(id, BigDecimal.ONE, instant, accountId);
            Deposit deposit = new Deposit(id, BigDecimal.ONE, instant, accountId);

            // when
            boolean result = withdrawal.equals(deposit);

            // then
            assertFalse(result);
        }
    }

    @Nested
    class TransferTest {

        @Test
        void should_return_true_when_does_relate_to_account() {

            // given
            UUID accountId = UUID.randomUUID();
            Transfer relatedTransferFromAccount = new Transfer(UUID.randomUUID(), BigDecimal.ONE, Instant.now(), accountId, UUID.randomUUID());
            Transfer relatedTransferToAccount = new Transfer(UUID.randomUUID(), BigDecimal.ONE, Instant.now(), UUID.randomUUID(), accountId);
            Transfer unrelatedTransfer = new Transfer(UUID.randomUUID(), BigDecimal.ONE, Instant.now(), UUID.randomUUID(), UUID.randomUUID());

            // when
            boolean relatedResult1 = relatedTransferFromAccount.relatesTo(accountId);
            boolean relatedResult2 = relatedTransferToAccount.relatesTo(accountId);
            boolean unrelatedResult = unrelatedTransfer.relatesTo(accountId);

            // then
            assertTrue(relatedResult1);
            assertTrue(relatedResult2);
            assertFalse(unrelatedResult);
        }

        @Test
        void should_return_same_hash_code_as_id() {

            // given
            UUID id = UUID.randomUUID();
            Transfer transfer = new Transfer(id, BigDecimal.ONE, Instant.now(), UUID.randomUUID(), UUID.randomUUID());

            // when
            int result = transfer.hashCode();

            // then
            assertThat(result)
                    .isEqualTo(id.hashCode());
        }

        @Test
        void should_be_equals_to_other_deposit_that_has_the_same_id() {

            // given
            UUID id = UUID.randomUUID();
            Transfer transfer1 = new Transfer(id, BigDecimal.ONE, Instant.now(), UUID.randomUUID(), UUID.randomUUID());
            Transfer transfer2 = new Transfer(id, BigDecimal.TWO, Instant.now().plusSeconds(2), UUID.randomUUID(), UUID.randomUUID());

            // when
            boolean result = transfer1.equals(transfer2);

            // then
            assertTrue(result);
        }

        @Test
        void should_not_be_equals_to_other_other_transaction_type() {

            // given
            UUID id = UUID.randomUUID();
            Instant instant = Instant.now();
            UUID accountId = UUID.randomUUID();
            Transfer transfer = new Transfer(id, BigDecimal.ONE, instant, accountId, UUID.randomUUID());
            Deposit deposit = new Deposit(id, BigDecimal.ONE, instant, accountId);

            // when
            boolean result = transfer.equals(deposit);

            // then
            assertFalse(result);
        }

        @Test
        void should_throw_exception_when_trying_to_create_transfer_to_the_same_account() {

            // given
            UUID accountId = UUID.randomUUID();

            // when
            ThrowableAssert.ThrowingCallable callable = () -> new Transfer(UUID.randomUUID(), BigDecimal.ONE, Instant.now(), accountId, accountId);

            // then
            assertThatThrownBy(callable)
                    .isInstanceOf(TransferToTheSameAccountException.class)
                    .hasMessage("From account and to account cannot be the same in a Transfer transaction. Account ID: [%s]".formatted(accountId));
        }
    }
}
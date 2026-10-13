package ricardocorreia.personal.tinyledger.domain.model;

import ricardocorreia.personal.tinyledger.domain.model.exceptions.TransferToTheSameAccountException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Deposit;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Transfer;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Withdrawal;

public sealed interface Transaction permits Deposit, Withdrawal, Transfer {

    UUID id();

    BigDecimal amount();

    Instant timestamp();

    boolean relatesTo(UUID accountId);

    record Deposit(UUID id, BigDecimal amount, Instant timestamp, UUID accountId) implements Transaction {

        public Deposit(BigDecimal amount, Instant timestamp, UUID accountId) {
            this(UUID.randomUUID(), amount, timestamp, accountId);
        }

        @Override
        public boolean relatesTo(UUID accountId) {
            return this.accountId.equals(accountId);
        }

        @Override
        public int hashCode() {
            return id().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Deposit other && this.id().equals(other.id());
        }
    }

    record Withdrawal(UUID id, BigDecimal amount, Instant timestamp, UUID accountId) implements Transaction {

        public Withdrawal(BigDecimal amount, Instant timestamp, UUID accountId) {
            this(UUID.randomUUID(), amount, timestamp, accountId);
        }

        @Override
        public boolean relatesTo(UUID accountId) {
            return this.accountId.equals(accountId);
        }

        @Override
        public int hashCode() {
            return id().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Withdrawal other && this.id().equals(other.id());
        }
    }

    record Transfer(UUID id,
                    BigDecimal amount,
                    Instant timestamp,
                    UUID fromAccountId,
                    UUID toAccountId) implements Transaction {

        public Transfer {
            validateDifferentAccount(fromAccountId, toAccountId);
        }

        public Transfer(BigDecimal amount, Instant timestamp, UUID fromAccountId, UUID toAccountId) {
            this(UUID.randomUUID(), amount, timestamp, fromAccountId, toAccountId);
        }

        @Override
        public boolean relatesTo(UUID accountId) {
            return this.fromAccountId.equals(accountId) || this.toAccountId.equals(accountId);
        }

        @Override
        public int hashCode() {
            return id().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Transfer other && this.id().equals(other.id());
        }

        private void validateDifferentAccount(UUID fromAccountId, UUID toAccountId) {
            if (fromAccountId.equals(toAccountId)) {
                throw new TransferToTheSameAccountException(fromAccountId);
            }
        }
    }
}

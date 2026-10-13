package ricardocorreia.personal.tinyledger.domain.model;

import lombok.Getter;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AmountIsNotPositiveException;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public final class Account {

    @Getter
    private final UUID id;
    @Getter
    private final String name;
    private BigDecimal balance;
    private final ReadWriteLock lock;

    public Account(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.balance = BigDecimal.ZERO;
        lock = new ReentrantReadWriteLock();
    }

    public BigDecimal deposit(BigDecimal amount) {
        validatePositiveAmount(amount);
        return doWithWriteLock(() -> unsafeAddBalance(amount));
    }

    public BigDecimal withdraw(BigDecimal amount) {
        validatePositiveAmount(amount);
        return doWithWriteLock(() -> unsafeSubtractBalance(amount));
    }

    public BigDecimal transfer(Account toAccount, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account first = this.id.compareTo(toAccount.id) > 0 ? this : toAccount;
        Account second = first.equals(this) ? toAccount : this;

        return first.doWithWriteLock(() ->
                second.doWithWriteLock(() -> {
                            this.unsafeSubtractBalance(amount);
                            toAccount.unsafeAddBalance(amount);
                            return this.balance;
                        }
                ));
    }

    private BigDecimal unsafeAddBalance(BigDecimal amount) {
        balance = balance.add(amount);
        return balance;
    }

    private BigDecimal unsafeSubtractBalance(BigDecimal amount) {
        validateEnoughBalance(amount);
        balance = balance.subtract(amount);
        return balance;
    }

    public BigDecimal getBalance() {
        try {
            lock.readLock().lock();
            return balance;
        } finally {
            lock.readLock().unlock();
        }
    }

    private <T> T doWithWriteLock(Supplier<T> supplier) {
        try {
            lock.writeLock().lock();
            return supplier.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Account other && this.id.equals(other.id);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AmountIsNotPositiveException(amount);
        }
    }

    private void validateEnoughBalance(BigDecimal amount) {
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientBalanceException(id, balance, amount);
        }
    }
}

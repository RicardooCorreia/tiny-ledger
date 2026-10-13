package ricardocorreia.personal.tinyledger.domain.model;

import lombok.Getter;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AmountIsNotPositiveException;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public final class Account {

    private final UUID id;
    private final String name;
    private BigDecimal balance;

    public Account(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.balance = BigDecimal.ZERO;
    }

    public BigDecimal deposit(BigDecimal amount) {
        validatePositiveAmount(amount);
        balance = balance.add(amount);
        return balance;
    }

    public BigDecimal withdraw(BigDecimal amount) {
        validatePositiveAmount(amount);
        validateEnoughBalance(amount);
        balance = balance.subtract(amount);
        return balance;
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

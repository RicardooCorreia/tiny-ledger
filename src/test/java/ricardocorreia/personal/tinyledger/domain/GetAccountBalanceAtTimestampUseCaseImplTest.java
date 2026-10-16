package ricardocorreia.personal.tinyledger.domain;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.openMocks;

class GetAccountBalanceAtTimestampUseCaseImplTest {

    @Mock
    AccountRepository accountRepository;
    @Mock
    TransactionRepository transactionRepository;

    private GetAccountBalanceAtTimestampUseCaseImpl subject;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new GetAccountBalanceAtTimestampUseCaseImpl(accountRepository, transactionRepository);
    }

    @Test
    void calculates_account_balance_at_time() {

        // given
        Instant instant = Instant.now();
        UUID accountId = UUID.randomUUID();

        doReturn(Optional.of(Instancio.create(Account.class)))
                .when(accountRepository)
                .find(accountId);

        Transaction.Deposit transaction1 = new Transaction.Deposit(BigDecimal.TEN, instant.minusSeconds(10), accountId);
        Transaction.Withdrawal transaction2 = new Transaction.Withdrawal(BigDecimal.ONE, instant.minusSeconds(5), accountId);
        Transaction.Withdrawal transaction3 = new Transaction.Withdrawal(BigDecimal.TWO, instant.plusSeconds(1), accountId);

        doReturn(List.of(transaction1, transaction2, transaction3))
                .when(transactionRepository)
                .findFor(accountId);

        // when
        BigDecimal result = subject.get(accountId, instant);

        // then
        assertThat(result)
                .isEqualTo(BigDecimal.valueOf(9));
    }

    @Test
    void calculates_account_balance_at_epoch() {

        // given
        Instant instant = Instant.EPOCH;
        UUID accountId = UUID.randomUUID();

        doReturn(Optional.of(Instancio.create(Account.class)))
                .when(accountRepository)
                .find(accountId);

        Instant currentInstant = Instant.now();
        Transaction.Deposit transaction1 = new Transaction.Deposit(BigDecimal.TEN, currentInstant, accountId);
        Transaction.Withdrawal transaction2 = new Transaction.Withdrawal(BigDecimal.ONE, currentInstant.minusSeconds(5), accountId);
        Transaction.Withdrawal transaction3 = new Transaction.Withdrawal(BigDecimal.TWO, currentInstant.plusSeconds(1), accountId);

        doReturn(List.of(transaction1, transaction2, transaction3))
                .when(transactionRepository)
                .findFor(accountId);

        // when
        BigDecimal result = subject.get(accountId, instant);

        // then
        assertThat(result)
                .isEqualTo(BigDecimal.ZERO);
    }
}
package ricardocorreia.personal.tinyledger.domain;

import org.assertj.core.api.ThrowableAssert;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.InstantProvider;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

class DepositAmountUseCaseImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private InstantProvider instantProvider;

    private DepositAmountUseCaseImpl subject;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new DepositAmountUseCaseImpl(accountRepository, transactionRepository, instantProvider);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(accountRepository, transactionRepository, instantProvider);
    }

    @Test
    void should_deposit_amount_when_account_exists() {

        // given
        UUID accountId = UUID.randomUUID();
        BigDecimal depositAmount = BigDecimal.TEN;
        Account account = Instancio.of(Account.class)
                .set(field(Account::getBalance), BigDecimal.ONE)
                .create();

        doReturn(Optional.of(account))
                .when(accountRepository)
                .find(accountId);

        Instant instant = Instant.now();
        doReturn(instant)
                .when(instantProvider)
                .instant();

        doAnswer(call -> call.getArgument(0))
                .when(transactionRepository)
                .save(any());

        // when
        Transaction.Deposit result = subject.deposit(accountId, depositAmount);

        // then
        assertThat(account.getBalance())
                .isEqualTo(BigDecimal.valueOf(11));
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(new Transaction.Deposit(result.id(), depositAmount, instant, accountId));
        verify(accountRepository).find(accountId);
        verify(transactionRepository).save(eq(result));
        verify(instantProvider).instant();
    }

    @Test
    void should_throw_when_no_account_is_found() {

        // given
        UUID accountId = UUID.randomUUID();
        doReturn(empty())
                .when(accountRepository)
                .find(any());

        // when
        ThrowableAssert.ThrowingCallable callable = () -> subject.deposit(accountId, BigDecimal.ONE);

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account with ID [%s] not found".formatted(accountId));
        verify(accountRepository).find(eq(accountId));
    }
}
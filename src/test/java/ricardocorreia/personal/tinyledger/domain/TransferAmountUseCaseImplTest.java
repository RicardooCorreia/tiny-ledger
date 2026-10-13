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
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AmountIsNotPositiveException;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.TransferToTheSameAccountException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.InstantProvider;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Transfer;

class TransferAmountUseCaseImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private InstantProvider instantProvider;

    private TransferAmountUseCaseImpl subject;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new TransferAmountUseCaseImpl(accountRepository, transactionRepository, instantProvider);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(accountRepository, transactionRepository, instantProvider);
    }

    @Test
    void should_transfer_funds_between_accounts() {

        // given
        Account fromAccount = Instancio.of(Account.class)
                .set(field(Account::getBalance), BigDecimal.TEN)
                .create();
        Account toAccount = Instancio.of(Account.class)
                .set(field(Account::getBalance), BigDecimal.TWO)
                .create();
        BigDecimal transferAmount = BigDecimal.valueOf(5);

        doReturn(Optional.of(fromAccount))
                .when(accountRepository)
                .find(fromAccount.getId());
        doReturn(Optional.of(toAccount))
                .when(accountRepository)
                .find(toAccount.getId());

        Instant instant = Instant.now();
        doReturn(instant)
                .when(instantProvider)
                .instant();

        doAnswer(call -> call.getArgument(0))
                .when(transactionRepository)
                .save(any());

        // when
        Transfer result = subject.transfer(fromAccount.getId(), toAccount.getId(), transferAmount);

        // then
        assertThat(fromAccount.getBalance())
                .isEqualTo(BigDecimal.valueOf(5));
        assertThat(toAccount.getBalance())
                .isEqualTo(BigDecimal.valueOf(7));
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(new Transaction.Transfer(result.id(), transferAmount, instant, fromAccount.getId(), toAccount.getId()));
        verify(accountRepository, times(2)).find(any());
        verify(transactionRepository).save(eq(result));
        verify(instantProvider).instant();
    }

    @Test
    void should_throw_when_from_account_is_not_found() {

        // given
        UUID fromAccountId = UUID.randomUUID();
        doReturn(Optional.empty())
                .when(accountRepository)
                .find(any());

        // when
        ThrowableAssert.ThrowingCallable callable = () -> subject.transfer(fromAccountId, UUID.randomUUID(), BigDecimal.ONE);

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account with ID [%s] not found".formatted(fromAccountId));
        verify(accountRepository).find(any());
    }

    @Test
    void should_throw_when_to_account_is_not_found() {

        // given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        doReturn(Optional.of(Instancio.create(Account.class)))
                .when(accountRepository)
                .find(fromAccountId);
        doReturn(Optional.empty())
                .when(accountRepository)
                .find(toAccountId);

        // when
        ThrowableAssert.ThrowingCallable callable = () -> subject.transfer(fromAccountId, toAccountId, BigDecimal.ONE);

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account with ID [%s] not found".formatted(toAccountId));
        verify(accountRepository, times(2)).find(any());
    }

    @Test
    void should_throw_when_transfer_amount_is_not_positive() {

        // given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        doReturn(Optional.of(Instancio.create(Account.class)))
                .when(accountRepository)
                .find(fromAccountId);
        doReturn(Optional.empty())
                .when(accountRepository)
                .find(toAccountId);

        // when
        ThrowableAssert.ThrowingCallable callable = () -> subject.transfer(fromAccountId, toAccountId, BigDecimal.ONE.negate());

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(AmountIsNotPositiveException.class)
                .hasMessage("Input amount needs to be positive, instead got [-1]");
    }

    @Test
    void should_throw_when_transferring_to_the_same_account() {

        // given
        UUID accountId = UUID.randomUUID();

        // when
        ThrowableAssert.ThrowingCallable callable = () -> subject.transfer(accountId, accountId, BigDecimal.ONE);

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(TransferToTheSameAccountException.class)
                .hasMessage("From account and to account cannot be the same in a Transfer transaction. Account ID: [%s]", accountId);
    }
}
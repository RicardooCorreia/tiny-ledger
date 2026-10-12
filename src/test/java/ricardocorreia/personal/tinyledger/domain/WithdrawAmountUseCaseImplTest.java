package ricardocorreia.personal.tinyledger.domain;

import org.assertj.core.api.ThrowableAssert;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.AccountNotFoundException;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class WithdrawAmountUseCaseImplTest {

    @Mock
    private AccountRepository accountRepository;

    private WithdrawAmountUseCaseImpl subject;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new WithdrawAmountUseCaseImpl(accountRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void should_deposit_amount_when_account_exists() {

        // given
        UUID accountId = UUID.randomUUID();
        BigDecimal withdrawAmount = BigDecimal.ONE;
        Account account = Instancio.of(Account.class)
                .set(field(Account::getBalance), BigDecimal.TEN)
                .create();

        doReturn(Optional.of(account))
                .when(accountRepository)
                .find(accountId);

        // when
        BigDecimal result = subject.withdraw(accountId, withdrawAmount);

        // then
        BigDecimal expected = BigDecimal.valueOf((9));
        assertThat(result)
                .isEqualTo(expected);
        verify(accountRepository).find(accountId);
    }

    @Test
    void should_throw_when_no_account_is_found() {

        // given
        UUID accountId = UUID.randomUUID();
        doReturn(empty())
                .when(accountRepository)
                .find(any());

        // when
        ThrowableAssert.ThrowingCallable callable = () -> subject.withdraw(accountId, BigDecimal.ONE);

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account with ID [%s] not found".formatted(accountId));
        verify(accountRepository).find(eq(accountId));
    }
}
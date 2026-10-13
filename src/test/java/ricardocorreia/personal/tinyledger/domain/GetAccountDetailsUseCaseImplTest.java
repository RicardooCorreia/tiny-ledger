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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

class GetAccountDetailsUseCaseImplTest {

    @Mock
    private AccountRepository accountRepository;

    private GetAccountDetailsUseCaseImpl subject;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new GetAccountDetailsUseCaseImpl(accountRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void should_get_account_when_account_exists() {

        // given
        UUID accountId = UUID.randomUUID();
        Account account = Instancio.create(Account.class);
        doReturn(Optional.of(account))
                .when(accountRepository)
                .find(accountId);

        // when
        Account result = subject.get(accountId);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(account);
        verify(accountRepository).find(accountId);
    }

    @Test
    void should_throw_exception_when_account_does_not_exist() {

        // given
        UUID accountId = UUID.randomUUID();
        doReturn(Optional.empty())
                .when(accountRepository)
                .find(any());

        // when
        ThrowableAssert.ThrowingCallable callable = () -> subject.get(accountId);

        // then
        assertThatThrownBy(callable)
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account with ID [%s] not found".formatted(accountId));
        verify(accountRepository).find(accountId);
    }
}
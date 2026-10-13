package ricardocorreia.personal.tinyledger.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

class CreateAccountUseCaseImplTest {

    @Mock
    private AccountRepository accountRepository;

    private CreateAccountUseCaseImpl subject;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new CreateAccountUseCaseImpl(accountRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void should_create_new_account() {

        // given
        String accountName = "Test account";
        doAnswer(call -> call.getArgument(0))
                .when(accountRepository)
                .upsert(any());

        // when
        Account result = subject.create(accountName);

        // then
        verify(accountRepository)
                .upsert(accountArgumentCaptor.capture());
        assertThat(accountArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(result);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(accountName);
        assertThat(result.getBalance()).isEqualTo(BigDecimal.ZERO);
    }
}
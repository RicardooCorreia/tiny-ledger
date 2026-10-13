package ricardocorreia.personal.tinyledger.domain;

import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

class ListTransactionsUseCaseImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    private ListTransactionsUseCaseImpl subject;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new ListTransactionsUseCaseImpl(transactionRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void should_list_all_the_transactions_for_account() {

        // given
        UUID accountId = UUID.randomUUID();
        Transaction.Deposit transaction1 = Instancio.create(Transaction.Deposit.class);
        Transaction.Transfer transaction2 = Instancio.create(Transaction.Transfer.class);

        doReturn(List.of(transaction1, transaction2))
                .when(transactionRepository)
                .findFor(accountId);

        // when
        List<Transaction> result = subject.find(accountId);

        // then
        assertThat(result)
                .containsExactly(transaction1, transaction2);
        verify(transactionRepository).findFor(accountId);
    }
}
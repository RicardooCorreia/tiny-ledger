package ricardocorreia.personal.tinyledger.adapters.persistence;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Deposit;

class LinkedQueueTransactionRepositoryTest {

    private LinkedQueueTransactionRepository subject;

    @BeforeEach
    void setUp() {
        subject = new LinkedQueueTransactionRepository();
    }

    @Test
    void should_save_new_transaction() {

        // given
        Deposit transaction = new Deposit(UUID.randomUUID(), BigDecimal.ONE, Instant.now(), UUID.randomUUID());

        // when
        Deposit result = subject.save(transaction);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(transaction);
        assertThat(subject.getAll())
                .containsExactly(result);
    }

    @Test
    void should_find_all_transactions_related_to_account() {

        // given
        UUID accountId = UUID.randomUUID();
        Deposit relatedDeposit = Instancio.of(Deposit.class)
                .set(field(Deposit::accountId), accountId)
                .create();
        Deposit unrelatedDeposit = Instancio.of(Deposit.class)
                .set(field(Deposit::accountId), UUID.randomUUID())
                .create();
        subject.save(relatedDeposit);
        subject.save(unrelatedDeposit);

        // when
        List<Transaction> result = subject.findFor(accountId);

        // then
        assertThat(result)
                .containsExactly(relatedDeposit);
    }

    @Test
    @Timeout(value = 5L, unit = TimeUnit.SECONDS)
    void should_save_all_transactions_when_writing_concurrently() {

        // given
        int transactionsToSave = 80_000;

        // when
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < transactionsToSave; i++) {
                executorService.submit(() -> subject.save(new Deposit(BigDecimal.ONE, Instant.EPOCH, UUID.randomUUID())));
            }
        }

        // then
        assertThat(subject.getAll())
                .hasSize(transactionsToSave);
    }
}

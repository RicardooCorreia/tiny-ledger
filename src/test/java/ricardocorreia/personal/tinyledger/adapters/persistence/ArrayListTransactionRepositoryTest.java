package ricardocorreia.personal.tinyledger.adapters.persistence;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Deposit;

class ArrayListTransactionRepositoryTest {

    private ArrayListTransactionRepository subject;

    @BeforeEach
    void setUp() {
        subject = new ArrayListTransactionRepository();
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
}
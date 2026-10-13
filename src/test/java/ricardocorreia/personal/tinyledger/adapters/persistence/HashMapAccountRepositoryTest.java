package ricardocorreia.personal.tinyledger.adapters.persistence;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ricardocorreia.personal.tinyledger.domain.model.Account;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

class HashMapAccountRepositoryTest {

    private HashMapAccountRepository subject;

    @BeforeEach
    void setUp() {
        subject = new HashMapAccountRepository();
    }

    @Test
    void should_save_new_account_when_does_not_exist() {

        // given
        Account account = Instancio.create(Account.class);

        // when
        Account result = subject.upsert(account);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(account)
                .isEqualTo(subject.find(account.getId()).orElseThrow());
    }

    @Test
    void should_update_existing_account() {

        // given
        Account account = Instancio.create(Account.class);
        subject.upsert(account);

        Account updatedAccount = Instancio.of(Account.class)
                .set(field(Account::getId), account.getId())
                .create();

        // when
        Account result = subject.upsert(updatedAccount);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(updatedAccount)
                .isEqualTo(subject.find(account.getId()).orElseThrow());
    }

    @Test
    void should_find_existing_account() {

        // given
        Account account = Instancio.create(Account.class);
        subject.upsert(account);

        // when
        Optional<Account> result = subject.find(account.getId());

        // then
        assertThat(result)
                .isNotEmpty()
                .hasValue(account);
    }

    @Test
    void should_return_empty_optional_when_does_not_find_id() {

        // when
        Optional<Account> result = subject.find(UUID.randomUUID());

        // then
        assertThat(result)
                .isEmpty();
    }

    @Test
    @Timeout(value = 5L, unit = TimeUnit.SECONDS)
    void should_save_all_accounts_when_writing_concurrently() {

        // given
        int iterations = 80_000;
        ConcurrentLinkedQueue<UUID> accountIds = new ConcurrentLinkedQueue<>();

        // when
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < iterations; i++) {
                Account account = new Account("account-" + i);
                accountIds.add(account.getId());
                executorService.submit(() -> subject.upsert(account));
            }
        }

        // then
        assertThat(subject.size())
                .isEqualTo(iterations);
        assertThat(accountIds)
                .allSatisfy(id -> assertThat(subject.find(id)).isPresent());
    }
}

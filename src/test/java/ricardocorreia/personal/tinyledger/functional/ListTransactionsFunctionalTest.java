package ricardocorreia.personal.tinyledger.functional;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Deposit;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Transfer;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.Withdrawal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ListTransactionsFunctionalTest {

    @LocalServerPort
    int port;

    RestTestClient client;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void should_list_transactions_related_to_account() {

        // given
        Account account = Instancio.create(Account.class);
        accountRepository.upsert(account);

        Deposit relatedDeposit = Instancio.of(Deposit.class)
                .set(field(Deposit::timestamp), Instant.now().plusMillis(100))
                .set(field(Deposit::accountId), account.getId())
                .create();
        Deposit unrelatedDeposit = Instancio.of(Deposit.class)
                .set(field(Deposit::accountId), UUID.randomUUID())
                .create();
        Withdrawal relatedWithdrawal = Instancio.of(Withdrawal.class)
                .set(field(Withdrawal::timestamp), Instant.now().plusMillis(200))
                .set(field(Withdrawal::accountId), account.getId())
                .create();
        Withdrawal unrelatedWithdrawal = Instancio.of(Withdrawal.class)
                .set(field(Withdrawal::accountId), UUID.randomUUID())
                .create();
        Transfer relatedTransfer1 = Instancio.of(Transfer.class)
                .set(field(Transfer::timestamp), Instant.now().plusMillis(300))
                .set(field(Transfer::fromAccountId), account.getId())
                .set(field(Transfer::toAccountId), UUID.randomUUID())
                .create();
        Transfer relatedTransfer2 = Instancio.of(Transfer.class)
                .set(field(Transfer::timestamp), Instant.now().plusMillis(400))
                .set(field(Transfer::fromAccountId), UUID.randomUUID())
                .set(field(Transfer::toAccountId), account.getId())
                .create();
        Transfer unrelatedTransfer = Instancio.of(Transfer.class)
                .set(field(Transfer::fromAccountId), UUID.randomUUID())
                .set(field(Transfer::toAccountId), UUID.randomUUID())
                .create();

        transactionRepository.save(relatedDeposit);
        transactionRepository.save(unrelatedDeposit);
        transactionRepository.save(relatedWithdrawal);
        transactionRepository.save(unrelatedWithdrawal);
        transactionRepository.save(relatedTransfer1);
        transactionRepository.save(relatedTransfer2);
        transactionRepository.save(unrelatedTransfer);

        RestTestClient.RequestHeadersSpec<?> request = client.get()
                .uri("/account/%s/transactions".formatted(account.getId()));

        // when
        RestTestClient.ResponseSpec result = request.exchange();

        // then
        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(4)
                .jsonPath("$[0].id").isEqualTo(relatedDeposit.id().toString())
                .jsonPath("$[0].type").isEqualTo("DEPOSIT")
                .jsonPath("$[0].amount").isEqualTo(relatedDeposit.amount().toString())
                .jsonPath("$[0].instant").isEqualTo(relatedDeposit.timestamp().toString())
                .jsonPath("$[0].fromAccountId").value(value -> assertThat(value).isNull())
                .jsonPath("$[0].toAccountId").isEqualTo(account.getId().toString())
                .jsonPath("$[1].id").isEqualTo(relatedWithdrawal.id().toString())
                .jsonPath("$[1].type").isEqualTo("WITHDRAWAL")
                .jsonPath("$[1].amount").isEqualTo(relatedWithdrawal.amount().toString())
                .jsonPath("$[1].instant").isEqualTo(relatedWithdrawal.timestamp().toString())
                .jsonPath("$[1].fromAccountId").isEqualTo(account.getId().toString())
                .jsonPath("$[1].toAccountId").value(value -> assertThat(value).isNull())
                .jsonPath("$[2].id").isEqualTo(relatedTransfer1.id().toString())
                .jsonPath("$[2].type").isEqualTo("TRANSFER")
                .jsonPath("$[2].amount").isEqualTo(relatedTransfer1.amount().toString())
                .jsonPath("$[2].instant").isEqualTo(relatedTransfer1.timestamp().toString())
                .jsonPath("$[2].fromAccountId").isEqualTo(account.getId().toString())
                .jsonPath("$[2].toAccountId").isEqualTo(relatedTransfer1.toAccountId().toString())
                .jsonPath("$[3].id").isEqualTo(relatedTransfer2.id().toString())
                .jsonPath("$[3].type").isEqualTo("TRANSFER")
                .jsonPath("$[3].amount").isEqualTo(relatedTransfer2.amount().toString())
                .jsonPath("$[3].instant").isEqualTo(relatedTransfer2.timestamp().toString())
                .jsonPath("$[3].fromAccountId").isEqualTo(relatedTransfer2.fromAccountId().toString())
                .jsonPath("$[3].toAccountId").isEqualTo(account.getId().toString());

        assertThat(transactionRepository.findFor(account.getId()))
                .containsExactly(relatedDeposit, relatedWithdrawal, relatedTransfer1, relatedTransfer2);
    }
}

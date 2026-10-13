package ricardocorreia.personal.tinyledger.functional;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransferFunctionalTest {

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
    void should_withdraw_when_has_enough_balance() {

        // given
        Account fromAccount = Instancio.of(Account.class)
                .set(field(Account::getBalance), BigDecimal.TEN)
                .create();
        Account toAccount = Instancio.of(Account.class)
                .set(field(Account::getBalance), BigDecimal.TWO)
                .create();
        accountRepository.upsert(fromAccount);
        accountRepository.upsert(toAccount);

        RestTestClient.RequestHeadersSpec<?> request = client.post()
                .uri("/transfers")
                .body(Map.of(
                        "fromAccountId", fromAccount.getId(),
                        "toAccountId", toAccount.getId(),
                        "amount", 3
                ));

        // when
        RestTestClient.ResponseSpec result = request.exchange();

        // then
        result
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/account/%s/transactions".formatted(fromAccount.getId()))
                .expectBody()
                .jsonPath("$.type").isEqualTo("TRANSFER")
                .jsonPath("$.amount").isEqualTo(3)
                .jsonPath("$.fromAccountId").isEqualTo(fromAccount.getId().toString())
                .jsonPath("$.toAccountId").isEqualTo(toAccount.getId().toString())
                .jsonPath("$.id").value(String.class, id -> {
                    Transaction.Transfer transfer = (Transaction.Transfer) transactionRepository.findFor(fromAccount.getId()).stream()
                            .filter(transaction -> transaction.id().toString().equals(id))
                            .findFirst().orElseThrow();
                    assertThat(transfer.amount()).isEqualByComparingTo("3");
                    assertThat(transfer.fromAccountId()).isEqualTo(fromAccount.getId());
                    assertThat(transfer.toAccountId()).isEqualTo(toAccount.getId());
                })
                .jsonPath("$.instant").value(String.class, instant -> {
                    Transaction.Transfer transfer = (Transaction.Transfer) transactionRepository.findFor(fromAccount.getId()).getFirst();
                    assertThat(instant).isEqualTo(transfer.timestamp().toString());
                });

        assertThat(accountRepository.find(fromAccount.getId()).orElseThrow().getBalance())
                .isEqualByComparingTo("7");
        assertThat(accountRepository.find(toAccount.getId()).orElseThrow().getBalance())
                .isEqualByComparingTo("5");
    }

    @Test
    void should_reject_transfer_when_has_no_enough_balance_in_from_account() {

        // given
        Account fromAccount = Instancio.of(Account.class)
                .set(field(Account::getBalance), BigDecimal.TEN)
                .create();
        Account toAccount = Instancio.of(Account.class)
                .set(field(Account::getBalance), BigDecimal.TWO)
                .create();
        accountRepository.upsert(fromAccount);
        accountRepository.upsert(toAccount);

        RestTestClient.RequestHeadersSpec<?> request = client.post()
                .uri("/transfers")
                .body(Map.of(
                        "fromAccountId", fromAccount.getId(),
                        "toAccountId", toAccount.getId(),
                        "amount", 20
                ));

        // when
        RestTestClient.ResponseSpec result = request.exchange();

        // then
        result.expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(String.class).value(body -> assertThat(body).contains("does not have enough balance"));
        assertThat(accountRepository.find(fromAccount.getId()).orElseThrow().getBalance())
                .isEqualByComparingTo("10");
        assertThat(accountRepository.find(toAccount.getId()).orElseThrow().getBalance())
                .isEqualByComparingTo("2");
        assertThat(transactionRepository.findFor(fromAccount.getId())).isEmpty();
        assertThat(transactionRepository.findFor(toAccount.getId())).isEmpty();
    }
}

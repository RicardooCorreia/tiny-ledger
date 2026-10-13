package ricardocorreia.personal.tinyledger.functional;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
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
public class DepositFunctionalTest {

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
    void should_deposit_amount() {

        // given
        Account account = Instancio.of(Account.class)
                .set(field(Account::getBalance), BigDecimal.TEN)
                .create();
        accountRepository.upsert(account);

        RestTestClient.RequestHeadersSpec<?> request = client.post()
                .uri("/account/%s/deposits".formatted(account.getId()))
                .body(Map.of(
                        "amount", 20
                ));

        // when
        RestTestClient.ResponseSpec result = request.exchange();

        // then
        result
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/account/%s/transactions".formatted(account.getId()))
                .expectBody()
                .jsonPath("$.type").isEqualTo("DEPOSIT")
                .jsonPath("$.amount").isEqualTo(20)
                .jsonPath("$.fromAccountId").doesNotExist()
                .jsonPath("$.toAccountId").isEqualTo(account.getId().toString())
                .jsonPath("$.id").value(String.class, id -> {
                    Transaction.Deposit deposit = (Transaction.Deposit) transactionRepository.findFor(account.getId()).stream()
                            .filter(transaction -> transaction.id().toString().equals(id))
                            .findFirst().orElseThrow();
                    assertThat(deposit.amount()).isEqualByComparingTo("20");
                    assertThat(deposit.accountId()).isEqualTo(account.getId());
                })
                .jsonPath("$.instant").value(String.class, instant -> {
                    Transaction.Deposit deposit = (Transaction.Deposit) transactionRepository.findFor(account.getId()).getFirst();
                    assertThat(instant).isEqualTo(deposit.timestamp().toString());
                });

        assertThat(accountRepository.find(account.getId()).orElseThrow().getBalance())
                .isEqualByComparingTo("30");
    }
}

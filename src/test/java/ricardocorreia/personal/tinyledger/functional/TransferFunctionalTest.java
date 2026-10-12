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

import java.math.BigDecimal;
import java.util.Map;

import static org.instancio.Select.field;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransferFunctionalTest {

    @LocalServerPort
    int port;

    RestTestClient client;

    @Autowired
    AccountRepository accountRepository;

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
                        "fromAccount", fromAccount.getId(),
                        "toAccount", toAccount.getId(),
                        "amount", 3
                ));

        // when
        RestTestClient.ResponseSpec result = request.exchange();

        // then
        result
                .expectStatus().isCreated();
        // TODO :: validate transaction response
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
                        "fromAccount", fromAccount.getId(),
                        "toAccount", toAccount.getId(),
                        "amount", 20
                ));

        // when
        RestTestClient.ResponseSpec result = request.exchange();

        // then
        result
                .expectStatus().is4xxClientError()
                .expectStatus().reasonEquals("Insufficient balance");
    }
}

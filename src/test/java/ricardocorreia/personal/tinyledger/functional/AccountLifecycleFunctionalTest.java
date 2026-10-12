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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountLifecycleFunctionalTest {

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
    void should_create_new_account() {

        // given
        RestTestClient.RequestHeadersSpec<?> request = client.post()
                .uri("/accounts")
                .body(Map.of(
                        "name", "New account"
                ));

        // when
        RestTestClient.ResponseSpec result = request.exchange();

        // then
        result
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("New account")
                .jsonPath("$.balance").isEqualTo("0")
                .jsonPath("$.id").value(UUID.class, id -> {
                    Account account = accountRepository.find(id).orElseThrow();
                    assertThat(account.getName()).isEqualTo("New account");
                    assertThat(account.getBalance()).isEqualTo(BigDecimal.ZERO);
                });
    }

    @Test
    void should_get_existing_account_and_present_values() {

        // given
        Account account = Instancio.create(Account.class);
        accountRepository.upsert(account);

        RestTestClient.RequestHeadersSpec<?> request = client.get()
                .uri("/accounts/%s".formatted(account.getId()));

        // when
        RestTestClient.ResponseSpec result = request.exchange();

        // then
        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(account.getId())
                .jsonPath("$.name").isEqualTo(account.getName())
                .jsonPath("$.balance").isEqualTo(account.getBalance());
    }
}

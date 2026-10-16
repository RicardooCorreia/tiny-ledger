package ricardocorreia.personal.tinyledger.adapters.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import ricardocorreia.personal.tinyledger.adapters.rest.handler.RestExceptionHandler;
import ricardocorreia.personal.tinyledger.adapters.rest.mapper.AccountMapper;
import ricardocorreia.personal.tinyledger.adapters.rest.response.AccountResponse;
import ricardocorreia.personal.tinyledger.domain.api.CreateAccountUseCase;
import ricardocorreia.personal.tinyledger.domain.api.GetAccountDetailsUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Account;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;

@WebMvcTest(AccountController.class)
@AutoConfigureRestTestClient
@Import(RestExceptionHandler.class)
class AccountControllerIntegrationTest {

    @MockitoBean
    private CreateAccountUseCase createAccountUseCase;
    @MockitoBean
    private GetAccountDetailsUseCase getAccountDetailsUseCase;
    @MockitoBean
    private AccountMapper accountMapper;

    @Autowired
    private RestTestClient client;

    @Test
    void should_create_account() {
        // given
        String accountName = "New Account";
        Account account = new Account(accountName);
        AccountResponse accountResponse = new AccountResponse(account.getId(), account.getName(), account.getBalance());
        doReturn(account)
                .when(createAccountUseCase)
                .create(account.getName());
        doReturn(accountResponse)
                .when(accountMapper)
                .map(account);

        // when
        RestTestClient.ResponseSpec response = client.post()
                .uri("/account")
                .body(Map.of("name", account.getName()))
                .exchange();

        // then
        response
                .expectStatus().isCreated()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/account/" + account.getId())
                .expectBody()
                .jsonPath("$.id").isEqualTo(account.getId().toString())
                .jsonPath("$.name").isEqualTo(accountName)
                .jsonPath("$.balance").isEqualTo(0);
    }

    @Test
    void should_get_account() {
        // given
        UUID accountId = UUID.randomUUID();
        String accountName = "New Account";
        Account account = new Account(accountName);
        AccountResponse accountResponse = new AccountResponse(accountId, account.getName(), account.getBalance());
        doReturn(Optional.of(account))
                .when(getAccountDetailsUseCase)
                .get(accountId);
        doReturn(accountResponse)
                .when(accountMapper)
                .map(account);

        // when
        RestTestClient.ResponseSpec response = client.get()
                .uri("/account/{id}", accountId)
                .exchange();

        // then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(accountId.toString())
                .jsonPath("$.name").isEqualTo(accountName)
                .jsonPath("$.balance").isEqualTo(0);
    }


    @Test
    void should_reject_null_account_id() {

        // when
        RestTestClient.ResponseSpec response = client.get()
                .uri("/account/null")
                .exchange();

        // then
        response.expectStatus().isBadRequest();
    }
}

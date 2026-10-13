package ricardocorreia.personal.tinyledger.adapters.rest.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import ricardocorreia.personal.tinyledger.adapters.rest.handler.RestExceptionHandler;
import ricardocorreia.personal.tinyledger.adapters.rest.mapper.TransactionMapper;
import ricardocorreia.personal.tinyledger.adapters.rest.response.TransactionResponse;
import ricardocorreia.personal.tinyledger.domain.api.DepositAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.api.ListTransactionsUseCase;
import ricardocorreia.personal.tinyledger.domain.api.TransferAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.api.WithdrawAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;

@WebMvcTest(TransactionController.class)
@AutoConfigureRestTestClient
@Import(RestExceptionHandler.class)
class TransactionControllerIntegrationTest {

    @MockitoBean
    private DepositAmountUseCase depositAmountUseCase;
    @MockitoBean
    private WithdrawAmountUseCase withdrawAmountUseCase;
    @MockitoBean
    private TransferAmountUseCase transferAmountUseCase;
    @MockitoBean
    private ListTransactionsUseCase listTransactionsUseCase;
    @MockitoBean
    private TransactionMapper transactionMapper;

    @Autowired
    private RestTestClient client;

    @Test
    void should_deposit() {
        // given
        BigDecimal amount = new BigDecimal(5);
        UUID accountId = UUID.randomUUID();
        Transaction.Deposit deposit = new Transaction.Deposit(amount, Instant.now(), accountId);
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(deposit.id())
                .type(TransactionResponse.Type.DEPOSIT)
                .amount(amount)
                .build();
        doReturn(deposit)
                .when(depositAmountUseCase)
                .deposit(accountId, amount);
        doReturn(transactionResponse)
                .when(transactionMapper)
                .map(deposit);

        // when
        RestTestClient.ResponseSpec response = client.post()
                .uri("/account/{id}/deposits", accountId)
                .body(Map.of("amount", amount))
                .exchange();

        // then
        response
                .expectStatus().isCreated()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/account/%s/transactions".formatted(accountId))
                .expectBody()
                .jsonPath("$.id").isEqualTo(deposit.id().toString())
                .jsonPath("$.type").isEqualTo("DEPOSIT")
                .jsonPath("$.amount").isEqualTo(5);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"amount\":null}", "{\"amount\":0}", "{\"amount\":-1}"})
    void should_reject_invalid_deposit_amount(
            // given
            String requestBody) {

        // when
        RestTestClient.ResponseSpec response = client.post()
                .uri("/account/{id}/deposits", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();

        // then
        response.expectStatus().isBadRequest();
    }

    @Test
    void should_withdraw() {
        // given
        BigDecimal amount = new BigDecimal(2);
        UUID accountId = UUID.randomUUID();
        Transaction.Withdrawal withdrawal = new Transaction.Withdrawal(amount, Instant.now(), accountId);
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(withdrawal.id())
                .type(TransactionResponse.Type.WITHDRAWAL)
                .amount(amount)
                .build();
        doReturn(withdrawal)
                .when(withdrawAmountUseCase)
                .withdraw(accountId, amount);
        doReturn(transactionResponse)
                .when(transactionMapper)
                .map(withdrawal);

        // when
        RestTestClient.ResponseSpec response = client.post()
                .uri("/account/{id}/withdrawals", accountId)
                .body(Map.of("amount", amount))
                .exchange();

        // then
        response
                .expectStatus().isCreated()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/account/%s/transactions".formatted(accountId))
                .expectBody()
                .jsonPath("$.id").isEqualTo(withdrawal.id().toString())
                .jsonPath("$.type").isEqualTo("WITHDRAWAL")
                .jsonPath("$.amount").isEqualTo(2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"amount\":null}", "{\"amount\":0}", "{\"amount\":-1}"})
    void should_reject_invalid_withdrawal_amount(
            // given
            String requestBody) {

        // when
        RestTestClient.ResponseSpec response = client.post()
                .uri("/account/{id}/withdrawals", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();

        // then
        response.expectStatus().isBadRequest();
    }

    @Test
    void should_transfer() {
        // given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(3);
        Transaction.Transfer transfer = new Transaction.Transfer(amount, Instant.now(), fromAccountId, toAccountId);
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(transfer.id())
                .type(TransactionResponse.Type.TRANSFER)
                .amount(amount)
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .build();
        doReturn(transfer)
                .when(transferAmountUseCase)
                .transfer(fromAccountId, toAccountId, amount);
        doReturn(transactionResponse)
                .when(transactionMapper)
                .map(transfer);

        // when
        RestTestClient.ResponseSpec response = client.post()
                .uri("/transfers")
                .body(Map.of(
                        "fromAccountId", fromAccountId,
                        "toAccountId", toAccountId,
                        "amount", amount))
                .exchange();

        // then
        response
                .expectStatus().isCreated()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/account/%s/transactions".formatted(fromAccountId))
                .expectBody()
                .jsonPath("$.id").isEqualTo(transfer.id().toString())
                .jsonPath("$.type").isEqualTo("TRANSFER")
                .jsonPath("$.amount").isEqualTo(3)
                .jsonPath("$.fromAccountId").isEqualTo(fromAccountId.toString())
                .jsonPath("$.toAccountId").isEqualTo(toAccountId.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}", // empty body
            "{\"amount\":1,\"fromAccountId\":\"8d6e9a39-6b18-4d06-9b70-c7cbfe31694a\"}", // missing toAccountId
            "{\"amount\":1,\"toAccountId\":\"5baf3bc7-4216-4ab1-9bd8-9b0156315e0b\"}", // missing fromAccountId
            "{\"fromAccountId\":\"8d6e9a39-6b18-4d06-9b70-c7cbfe31694a\",\"toAccountId\":\"5baf3bc7-4216-4ab1-9bd8-9b0156315e0b\"}", // missing amount
            "{\"amount\":null,\"fromAccountId\":\"8d6e9a39-6b18-4d06-9b70-c7cbfe31694a\",\"toAccountId\":\"5baf3bc7-4216-4ab1-9bd8-9b0156315e0b\"}", // null amount
            "{\"amount\":0,\"fromAccountId\":\"8d6e9a39-6b18-4d06-9b70-c7cbfe31694a\",\"toAccountId\":\"5baf3bc7-4216-4ab1-9bd8-9b0156315e0b\"}", // zero amount
            "{\"amount\":-1,\"fromAccountId\":\"8d6e9a39-6b18-4d06-9b70-c7cbfe31694a\",\"toAccountId\":\"5baf3bc7-4216-4d06-9b70-c7cbfe31694a\"}" // negative amount
    })
    void should_reject_invalid_transfer_request(
            // given
            String requestBody) {

        // when
        RestTestClient.ResponseSpec response = client.post()
                .uri("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();

        // then
        response.expectStatus().isBadRequest();
    }

    @Test
    void should_list_account_transactions() {
        // given
        UUID accountId = UUID.randomUUID();
        Transaction.Deposit deposit = new Transaction.Deposit(BigDecimal.ONE, Instant.EPOCH, accountId);
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(deposit.id()).type(TransactionResponse.Type.DEPOSIT).amount(deposit.amount()).build();
        doReturn(List.of(deposit)).when(listTransactionsUseCase).find(accountId);
        doReturn(transactionResponse).when(transactionMapper).map(deposit);

        // when
        RestTestClient.ResponseSpec response = client.get()
                .uri("/account/{id}/transactions", accountId)
                .exchange();

        // then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(deposit.id().toString())
                .jsonPath("$[0].type").isEqualTo("DEPOSIT")
                .jsonPath("$[0].amount").isEqualTo(1);
    }
}

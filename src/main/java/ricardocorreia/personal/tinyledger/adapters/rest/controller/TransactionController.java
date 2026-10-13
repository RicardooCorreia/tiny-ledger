package ricardocorreia.personal.tinyledger.adapters.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ricardocorreia.personal.tinyledger.adapters.rest.mapper.TransactionMapper;
import ricardocorreia.personal.tinyledger.adapters.rest.request.DepositRequest;
import ricardocorreia.personal.tinyledger.adapters.rest.request.TransferRequest;
import ricardocorreia.personal.tinyledger.adapters.rest.request.WithdrawalRequest;
import ricardocorreia.personal.tinyledger.adapters.rest.response.TransactionResponse;
import ricardocorreia.personal.tinyledger.domain.api.DepositAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.api.ListTransactionsUseCase;
import ricardocorreia.personal.tinyledger.domain.api.TransferAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.api.WithdrawAmountUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final DepositAmountUseCase depositAmountUseCase;
    private final WithdrawAmountUseCase withdrawAmountUseCase;
    private final TransferAmountUseCase transferAmountUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final TransactionMapper transactionMapper;

    @Operation(
            summary = "List account transactions",
            description = "Returns transactions involving the specified account, in repository order.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = TransactionResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Account with ID [550e8400-e29b-41d4-a716-446655440000] not found")))
    })
    @GetMapping("/account/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> listAccountTransactions(@PathVariable UUID accountId) {
        List<TransactionResponse> response = listTransactionsUseCase.find(accountId)
                .stream()
                .map(transactionMapper::map)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deposit funds", description = "Adds a positive amount to the account and records a deposit transaction.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Deposit recorded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed or amount is not positive",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Input amount needs to be positive, instead got [-1]"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Account with ID [550e8400-e29b-41d4-a716-446655440000] not found")))
    })
    @PostMapping("/account/{accountId}/deposits")
    public ResponseEntity<?> deposit(@PathVariable UUID accountId, @RequestBody @Valid DepositRequest depositRequest) {

        Transaction.Deposit deposit = depositAmountUseCase.deposit(accountId, depositRequest.getAmount());
        return ResponseEntity.created(URI.create("/account/%s/transactions".formatted(deposit.accountId())))
                .body(transactionMapper.map(deposit));
    }

    @Operation(summary = "Withdraw funds", description = "Withdraws a positive amount from the account and records a withdrawal transaction.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Withdrawal recorded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed or amount is not positive",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Input amount needs to be positive, instead got [-1]"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Account with ID [550e8400-e29b-41d4-a716-446655440000] not found"))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Insufficient balance",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Account with ID [550e8400-e29b-41d4-a716-446655440000] does not have enough balance. Current: [10], requested: [25], difference: [15]")))
    })
    @PostMapping("/account/{accountId}/withdrawals")
    public ResponseEntity<?> withdrawal(@PathVariable UUID accountId,
                                        @RequestBody @Valid WithdrawalRequest withdrawalRequest) {

        Transaction.Withdrawal withdrawal = withdrawAmountUseCase.withdraw(accountId, withdrawalRequest.getAmount());
        return ResponseEntity.created(URI.create("/account/%s/transactions".formatted(withdrawal.accountId())))
                .body(transactionMapper.map(withdrawal));
    }

    @Operation(summary = "Transfer funds", description = "Transfers a positive amount from one account to another.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transfer recorded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed, amount is not positive, or source and destination are the same",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "From account and to account cannot be the same in a Transfer transaction. Account ID: [550e8400-e29b-41d4-a716-446655440000]"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Source or destination account not found",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Account with ID [550e8400-e29b-41d4-a716-446655440000] not found"))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Insufficient balance",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Account with ID [550e8400-e29b-41d4-a716-446655440000] does not have enough balance. Current: [10], requested: [25], difference: [15]")))
    })
    @PostMapping("/transfers")
    public ResponseEntity<?> transfer(@RequestBody @Valid TransferRequest transferRequest) {

        Transaction.Transfer deposit = transferAmountUseCase
                .transfer(transferRequest.getFromAccountId(), transferRequest.getToAccountId(), transferRequest.getAmount());
        return ResponseEntity.created(URI.create("/account/%s/transactions".formatted(transferRequest.getFromAccountId())))
                .body(transactionMapper.map(deposit));
    }
}

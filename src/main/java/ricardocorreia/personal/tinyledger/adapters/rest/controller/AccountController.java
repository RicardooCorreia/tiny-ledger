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
import ricardocorreia.personal.tinyledger.adapters.rest.mapper.AccountMapper;
import ricardocorreia.personal.tinyledger.adapters.rest.request.CreateAccountRequest;
import ricardocorreia.personal.tinyledger.adapters.rest.response.AccountResponse;
import ricardocorreia.personal.tinyledger.domain.api.CreateAccountUseCase;
import ricardocorreia.personal.tinyledger.domain.api.GetAccountDetailsUseCase;
import ricardocorreia.personal.tinyledger.domain.model.Account;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountDetailsUseCase getAccountDetailsUseCase;
    private final AccountMapper accountMapper;

    @Operation(summary = "Create an account", description = "Creates an account with a zero balance.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed")
    })
    @PostMapping("/account")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody @Valid CreateAccountRequest createAccountRequest) {
        Account account = createAccountUseCase.create(createAccountRequest.getName());
        return ResponseEntity
                .created(URI.create("/account/%s".formatted(account.getId())))
                .body(accountMapper.map(account));
    }

    @Operation(summary = "Get an account", description = "Returns the account details and current balance.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Account with ID [550e8400-e29b-41d4-a716-446655440000] not found")))
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID accountId) { // TODO :: could be null?
        Account account = getAccountDetailsUseCase.get(accountId);
        return ResponseEntity.ok(accountMapper.map(account));
    }
}

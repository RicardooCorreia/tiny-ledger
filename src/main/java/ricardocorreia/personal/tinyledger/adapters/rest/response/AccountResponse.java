package ricardocorreia.personal.tinyledger.adapters.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Account details and current balance.")
public record AccountResponse(
        @Schema(description = "Unique account identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        @Schema(description = "Account holder or account name", example = "Savings")
        String name,
        @Schema(description = "Current balance", example = "125.50", minimum = "0")
        BigDecimal balance) {
}

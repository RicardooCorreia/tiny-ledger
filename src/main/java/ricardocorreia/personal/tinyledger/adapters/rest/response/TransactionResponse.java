package ricardocorreia.personal.tinyledger.adapters.rest.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder(builderClassName = "Builder")
@Schema(description = "A deposit, withdrawal, or transfer recorded for an account.")
public class TransactionResponse {
    @Schema(description = "Unique transaction identifier.", example = "550e8400-e29b-41d4-a716-446655440001")
    UUID id;
    @Schema(description = "Transaction type. Can be DEPOSIT, WITHDRAWAL or TRANSFER", example = "DEPOSIT")
    Type type;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Positive transaction amount.", example = "125.50", minimum = "0.01")
    BigDecimal amount;
    @Schema(description = "Time the transaction was recorded in UTC.", example = "2026-10-13T09:30:00Z", format = "date-time")
    Instant instant;
    @Schema(description = "Source account. Null for deposits.", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
    UUID fromAccountId;
    @Schema(description = "Destination account. Null for withdrawals.", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
    UUID toAccountId;

    @Schema(description = "Transaction kind.")
    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }
}

package ricardocorreia.personal.tinyledger.adapters.rest.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.InsufficientBalanceException;
import ricardocorreia.personal.tinyledger.domain.model.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RestExceptionHandlerTest {

    private final RestExceptionHandler subject = new RestExceptionHandler();

    @Test
    void should_handle_not_found() {
        // given
        ResourceNotFoundException exception = new ResourceNotFoundException("Account not found");

        // when
        var response = subject.handleNotFound(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Account not found");
    }

    @Test
    void should_handle_bad_request() {
        // given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid request");

        // when
        var response = subject.handleBadRequest(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid request");
    }

    @Test
    void should_handle_insufficient_balance() {
        // given
        UUID accountId = UUID.randomUUID();
        InsufficientBalanceException exception = new InsufficientBalanceException(
                accountId, BigDecimal.ZERO, BigDecimal.ONE);

        // when
        var response = subject.handleConflict(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo(exception.getMessage());
    }
}

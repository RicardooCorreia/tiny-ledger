package ricardocorreia.personal.tinyledger.adapters.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank
    private String name;
}

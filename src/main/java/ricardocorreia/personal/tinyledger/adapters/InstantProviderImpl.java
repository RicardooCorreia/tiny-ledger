package ricardocorreia.personal.tinyledger.adapters;

import org.springframework.stereotype.Component;
import ricardocorreia.personal.tinyledger.ports.api.InstantProvider;

import java.time.Instant;

@Component
public class InstantProviderImpl implements InstantProvider {

    @Override
    public Instant instant() {
        return Instant.now();
    }
}

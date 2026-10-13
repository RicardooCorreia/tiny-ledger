package ricardocorreia.personal.tinyledger.adapters;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InstantProviderImplTest {

    private final InstantProviderImpl subject = new InstantProviderImpl();

    @Test
    void should_return_current_instant() {

        // given
        Instant before = Instant.now();

        // when
        Instant result = subject.instant();

        // then
        Instant after = Instant.now();
        assertThat(result)
                .isBetween(before, after);
    }
}
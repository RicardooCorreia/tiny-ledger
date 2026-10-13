package ricardocorreia.personal.tinyledger.adapters.rest.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import ricardocorreia.personal.tinyledger.adapters.rest.response.TransactionResponse;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final TransactionMapper subject = new TransactionMapper();

    @Test
    void should_map_deposit_to_response() {
        // given
        Transaction.Deposit deposit = Instancio.create(Transaction.Deposit.class);

        // when
        TransactionResponse result = subject.map(deposit);

        // then
        assertThat(result)
                .isEqualTo(TransactionResponse.builder()
                        .id(deposit.id())
                        .type(TransactionResponse.Type.DEPOSIT)
                        .amount(deposit.amount())
                        .instant(deposit.timestamp())
                        .toAccountId(deposit.accountId())
                        .build());
    }

    @Test
    void should_map_withdrawal_to_response() {
        // given
        Transaction.Withdrawal withdrawal = Instancio.create(Transaction.Withdrawal.class);

        // when
        TransactionResponse result = subject.map(withdrawal);

        // then
        assertThat(result)
                .isEqualTo(TransactionResponse.builder()
                        .id(withdrawal.id())
                        .type(TransactionResponse.Type.WITHDRAWAL)
                        .amount(withdrawal.amount())
                        .instant(withdrawal.timestamp())
                        .fromAccountId(withdrawal.accountId())
                        .build());
    }

    @Test
    void should_map_transfer_to_response() {
        // given
        Transaction.Transfer transfer = Instancio.create(Transaction.Transfer.class);

        // when
        TransactionResponse result = subject.map(transfer);

        // then
        assertThat(result)
                .isEqualTo(TransactionResponse.builder()
                        .id(transfer.id())
                        .type(TransactionResponse.Type.TRANSFER)
                        .amount(transfer.amount())
                        .instant(transfer.timestamp())
                        .fromAccountId(transfer.fromAccountId())
                        .toAccountId(transfer.toAccountId())
                        .build());
    }
}

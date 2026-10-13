package ricardocorreia.personal.tinyledger.adapters.rest.mapper;

import org.springframework.stereotype.Component;
import ricardocorreia.personal.tinyledger.adapters.rest.response.TransactionResponse;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;

@Component
public class TransactionMapper {

    public TransactionResponse map(Transaction transaction) {

        TransactionResponse.Builder builder = TransactionResponse.builder()
                .id(transaction.id())
                .amount(transaction.amount())
                .instant(transaction.timestamp());

        return switch (transaction) {
            case Transaction.Deposit deposit -> builder
                    .type(TransactionResponse.Type.DEPOSIT)
                    .toAccountId(deposit.accountId())
                    .build();
            case Transaction.Withdrawal withdrawal -> builder
                    .type(TransactionResponse.Type.WITHDRAWAL)
                    .fromAccountId(withdrawal.accountId())
                    .build();
            case Transaction.Transfer transfer -> builder
                    .type(TransactionResponse.Type.TRANSFER)
                    .fromAccountId(transfer.fromAccountId())
                    .toAccountId(transfer.toAccountId())
                    .build();
        };
    }
}

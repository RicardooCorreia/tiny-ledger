package ricardocorreia.personal.tinyledger.functional;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;
import ricardocorreia.personal.tinyledger.domain.model.Account;
import ricardocorreia.personal.tinyledger.domain.model.Transaction;
import ricardocorreia.personal.tinyledger.ports.api.AccountRepository;
import ricardocorreia.personal.tinyledger.ports.api.TransactionRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.instancio.Select.field;
import static ricardocorreia.personal.tinyledger.domain.model.Transaction.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ListTransactionsFunctionalTest {

    @LocalServerPort
    int port;

    RestTestClient client;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void should_withdraw_when_has_enough_balance() {

        // given
        Account account = Instancio.create(Account.class);
        accountRepository.upsert(account);

        Deposit relatedDeposit = Instancio.of(Deposit.class)
                .set(field(Deposit::accountId), account.getId())
                .create();
        Deposit unrelatedDeposit = Instancio.of(Deposit.class)
                .set(field(Deposit::accountId), UUID.randomUUID())
                .create();
        Withdrawal relatedWithdrawal = Instancio.of(Withdrawal.class)
                .set(field(Withdrawal::accountId), account.getId())
                .create();
        Withdrawal unrelatedWithdrawal = Instancio.of(Withdrawal.class)
                .set(field(Withdrawal::accountId), UUID.randomUUID())
                .create();
        Transfer relatedTransfer1 = Instancio.of(Transfer.class)
                .set(field(Transfer::fromAccountId), account.getId())
                .set(field(Transfer::toAccountId), UUID.randomUUID())
                .create();
        Transfer relatedTransfer2 = Instancio.of(Transfer.class)
                .set(field(Transfer::fromAccountId), UUID.randomUUID())
                .set(field(Transfer::toAccountId), account.getId())
                .create();
        Transfer unrelatedTransfer = Instancio.of(Transfer.class)
                .set(field(Transfer::fromAccountId), UUID.randomUUID())
                .set(field(Transfer::toAccountId), UUID.randomUUID())
                .create();

        transactionRepository.save(relatedDeposit);
        transactionRepository.save(unrelatedDeposit);
        transactionRepository.save(relatedWithdrawal);
        transactionRepository.save(unrelatedWithdrawal);
        transactionRepository.save(relatedTransfer1);
        transactionRepository.save(relatedTransfer2);
        transactionRepository.save(unrelatedTransfer);

        RestTestClient.RequestHeadersSpec<?> request = client.get()
                .uri("/accounts/%s/transactions".formatted(account.getId()));

        // when
        RestTestClient.ResponseSpec result = request.exchange();

        // then
        result
                .expectStatus().isOk();
        // TODO :: validate transaction response
    }
}

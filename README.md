
# Tiny Ledger Application
A simple REST API for recording money movements, retrieving the current balance, and viewing transaction history.

## Functional Requirements
- Record deposits and withdrawals.
- Retrieve the current ledger balance.
- Retrieve transaction history.
- Account creation

## Non-Functional Requirements
- **REST API:** All interactions are exposed through HTTP endpoints.
- **In-memory storage:** Application state is stored in memory and is lost when the application stops.

## Out of scope
- Authentication and authorization
- Persistent data layer
- Logging and monitoring
- Atomicity and multi thread safety (!)
- Money multi currency transfers
- Account management and deletion
- Caching and other types of performance optimization

## API (Draft)
- Create account: `POST /accounts?name={name}` -> AccountResponse
- Deposit: `POST /accounts/{id}/deposits?amount={amount}` -> TransactionResponse
- Withdraw: `POST /accounts/{id}/withdrawals?amount={amount}` -> TransactionResponse
- Transfer: `POST /transfers?amount={amount}&toAccount={toAccountId}` -> TransactionResponse
- Check balance: `GET /accounts/{id}` -> AccountResponse
- Check transactions: `GET /accounts/{id}/transactions` -> List<TransactionResponse>

### AccountResponse
```json
{
  "id": "UUID",
  "name": "Account name",
  "balance": 123
}
```

### TransactionResponse

```json
{
  "id": "UUID",
  "fromAccount": {
    "id": "UUID",
    "name": "From account name"
  },
  "toAccount": {
    "id": "UUID",
    "name": "To account name"
  },
  "amount": 123
}
```


package ricardocorreia.personal.tinyledger.scripts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class SampleData {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private SampleData(String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 1) {
            throw new IllegalArgumentException("Usage: SampleDataSeeder [base-url]");
        }
        String baseUrl = args.length == 0 ? "http://localhost:8080" : args[0];
        new SampleData(baseUrl).seed();
    }

    private void seed() throws IOException, InterruptedException {
        SampleAccount everyday = createAccount("Everyday");
        SampleAccount savings = createAccount("Savings");
        SampleAccount travel = createAccount("Travel");
        SampleAccount emergency = createAccount("Emergency");

        deposit(everyday, "1000.00");
        deposit(savings, "500.00");
        deposit(travel, "250.00");
        deposit(emergency, "100.00");

        withdraw(everyday, "75.00");
        withdraw(travel, "25.00");

        transfer(everyday, savings, "150.00");
        transfer(savings, emergency, "50.00");

        System.out.println("Sample data created successfully.");
    }

    private SampleAccount createAccount(String name) throws IOException, InterruptedException {
        HttpResponse<String> response = post("/account", Map.of("name", name), "Create account " + name);
        JsonNode accountJson = objectMapper.readTree(response.body());
        JsonNode id = accountJson.get("id");
        if (id == null || !id.isTextual()) {
            throw new IllegalStateException("Create account " + name + " returned no account ID: " + response.body());
        }
        SampleAccount account = new SampleAccount(name, UUID.fromString(id.asText()));
        System.out.printf("Account %-10s %s%n", account.name(), account.id());
        return account;
    }

    private void deposit(SampleAccount account, String amount) throws IOException, InterruptedException {
        post("/account/%s/deposits".formatted(account.id()), amountBody(amount), "Deposit into " + account.name());
    }

    private void withdraw(SampleAccount account, String amount) throws IOException, InterruptedException {
        post("/account/%s/withdrawals".formatted(account.id()), amountBody(amount), "Withdraw from " + account.name());
    }

    private void transfer(SampleAccount from, SampleAccount to, String amount) throws IOException, InterruptedException {
        post("/transfers", Map.of(
                "fromAccountId", from.id(),
                "toAccountId", to.id(),
                "amount", new BigDecimal(amount)),
                "Transfer from " + from.name() + " to " + to.name());
    }

    private Map<String, BigDecimal> amountBody(String amount) {
        Map<String, BigDecimal> body = new LinkedHashMap<>();
        body.put("amount", new BigDecimal(amount));
        return body;
    }

    private HttpResponse<String> post(String path, Map<String, ?> body, String operation)
            throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new IllegalStateException("%s failed with HTTP %d: %s"
                    .formatted(operation, response.statusCode(), response.body()));
        }
        return response;
    }

    private record SampleAccount(String name, UUID id) {
    }
}

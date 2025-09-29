package com.merbsconnect.sms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merbsconnect.sms.dtos.request.BulkSmsRequest;
import com.merbsconnect.sms.dtos.request.CreateTemplateRequest;
import com.merbsconnect.sms.dtos.response.BulkSmsResponse;
import com.merbsconnect.sms.dtos.response.TemplateResponse;
import com.merbsconnect.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    private final ObjectMapper objectMapper;

    @Value("${app.sms.mnotify.api-key}")
    private String apiKey;

    @Value("${app.sms.mnotify.url}")
    private String apiUrl;

    private final static String SENDER_ID = "MerbConnect";

    @Override
    public void sendSms() {

    }

    @Override
    public TemplateResponse createTemplate(CreateTemplateRequest request) throws IOException, InterruptedException {

        String apiEndpoint = "/template";

        String urlWithParams = String.format("%s%s?key=%s&title=%s&body=%s", apiUrl, apiEndpoint, apiKey,URLEncoder.encode(request.getTitle(), StandardCharsets.UTF_8),URLEncoder.encode(request.getBody(),StandardCharsets.UTF_8));
        log .info("URL with params: {}", urlWithParams);
        HttpClient client = HttpClient.newHttpClient();
        log.info("Creating template with title: {}, content: {}", request.getTitle(), request.getBody());

        HttpRequest http = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .POST(HttpRequest.BodyPublishers.ofString(String.format("title=%s&body=%s",request.getTitle(),request.getBody())))
                .header("Content-Type","application/json")
                .build();
        HttpResponse<String> response = client.send(http, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), TemplateResponse.class);
    }


    /**
     * Fetches all templates from the mNotify API.
     *
     * @return A list of templates.
     * @throws IOException          If there is an issue with the HTTP request.
     * @throws InterruptedException If the request is interrupted.
     */
    public TemplateResponse getAllTemplates() throws IOException, InterruptedException {
        String apiEndpoint = "/template";
        String url = apiUrl + apiEndpoint + "?key=" + apiKey;
        log.info("Fetching templates from URL: {}", url);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.info("API response: {}", response.body());

        // Parse the response into a TemplateResponse object
        TemplateResponse templateResponse = objectMapper.readValue(response.body(), TemplateResponse.class);

        // Check if the response is successful and return the template list
        if (templateResponse.isSuccessful()) {
            return templateResponse;
        } else {
            throw new RuntimeException("Failed to fetch templates: " + templateResponse.getMessage());
        }
    }

    /**
     * Fetches a template by its ID from the mNotify API.
     *
     * @param templateId The ID of the template to fetch.
     * @return The TemplateResponse containing the template data.
     * @throws IOException          If there is an issue with the HTTP request.
     * @throws InterruptedException If the request is interrupted.
     */
    public TemplateResponse getTemplateById(String templateId) throws IOException, InterruptedException {
        String apiEndpoint = "/template/" + templateId;
        String url = apiUrl + apiEndpoint + "?key=" + apiKey;
        log.info("Fetching template by ID from URL: {}", url);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.info("API response: {}", response.body());

        TemplateResponse templateResponse = objectMapper.readValue(response.body(), TemplateResponse.class);

        if (templateResponse.isSuccessful()) {
            return templateResponse;
        } else {
            throw new RuntimeException("Failed to fetch template: " + templateResponse.getMessage());
        }
    }


    @Override
    public BulkSmsResponse sendBulkSms(BulkSmsRequest bulkSmsRequest) {
        try {
            return sendBulkSmsAsync(bulkSmsRequest).get(); // Blocking call to get the result
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to send bulk SMS: {}", e.getMessage());
            throw new RuntimeException("Failed to send bulk SMS", e);
        }
    }

    @Async
    private CompletableFuture<BulkSmsResponse> sendBulkSmsAsync(BulkSmsRequest bulkSmsRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                bulkSmsRequest.setSender(SENDER_ID);
                String requestBody = objectMapper.writeValueAsString(bulkSmsRequest);
                log.info("Request body: {}", requestBody);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl + "/sms/quick?key=" + apiKey))
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                log.info("API response: {}", response.body());

                BulkSmsResponse smsResponse = objectMapper.readValue(response.body(), BulkSmsResponse.class);
                if (smsResponse.isSuccessful()) {
                    return smsResponse;
                } else {
                    throw new RuntimeException("SMS API Error: " + smsResponse.getMessage());
                }
            } catch (IOException | InterruptedException e) {
                log.error("Error executing SMS request: {}", e.getMessage());
                throw new RuntimeException("Failed to send SMS", e);
            }
        });
    }





}

package com.merbsconnect.sms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merbsconnect.sms.dtos.request.CreateTemplateRequest;
import com.merbsconnect.sms.dtos.response.TemplateResponse;
import com.merbsconnect.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Value("${app.sms.mnotify.api-key}")
    private String apiKey;
    
    @Value("${app.sms.mnotify.url}")
    private String apiUrl;

    private final static String SENDER_ID = "MerbConnect";


    public TemplateResponse createTemplate(CreateTemplateRequest request) throws IOException, InterruptedException {

        String apiEndpoint = "/template";
        
        String urlWithParams = String.format("%s%s?key=%s&title=%s&content=%s", apiUrl, apiEndpoint, apiKey,request.getTitle(),URLEncoder.encode(request.getContent(),StandardCharsets.UTF_8));
        log .info("URL with params: {}", urlWithParams);
        HttpClient client = HttpClient.newHttpClient();
        log.info("Creating template with title: {}, content: {}", request.getTitle(), request.getContent());

        HttpRequest http = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .POST(HttpRequest.BodyPublishers.ofString(String.format("title=%s&content=%s",request.getTitle(),request.getContent())))
                .header("Content-Type","application/json")
                .build();
        HttpResponse<String> response = client.send(http, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), TemplateResponse.class);
    }

    @Override
    public TemplateResponse send(CreateTemplateRequest request) throws IOException, InterruptedException {
        return null;
    }
}

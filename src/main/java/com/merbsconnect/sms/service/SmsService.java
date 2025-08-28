package com.merbsconnect.sms.service;

import com.merbsconnect.sms.dtos.request.*;
import com.merbsconnect.sms.dtos.response.*;
import com.merbsconnect.sms.domain.SmsLog;
import com.merbsconnect.sms.domain.SmsTemplate;
import com.merbsconnect.events.model.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SmsService {

    // Template Management
    TemplateResponse createTemplate(CreateTemplateRequest request) throws IOException, InterruptedException;
    TemplateResponse updateTemplate(Long templateId, UpdateTemplateRequest request) throws IOException, InterruptedException;
    void deleteTemplate(Long templateId) throws IOException, InterruptedException;
    List<SmsTemplate> getAllTemplates();
    SmsTemplate getTemplateById(Long templateId);

    // SMS Sending
    SmsResponse sendSms(SendSmsRequest request) throws IOException, InterruptedException;
    BulkSmsResponse sendBulkSms(BulkSmsRequest request) throws IOException, InterruptedException;
    
    // Registration Confirmation
    void sendRegistrationConfirmation(Registration registration) throws IOException, InterruptedException;
    
    // Event Reminders
    void sendEventReminder(Long eventId, Long templateId, Integer daysBeforeEvent);
    void scheduleEventReminders(Long eventId, List<Integer> reminderDays);
    
    // Bulk Operations
    BulkSmsResponse sendToEventParticipants(Long eventId, Long templateId, List<Long> participantIds);
    BulkSmsResponse sendToAllEventParticipants(Long eventId, Long templateId);
    
    // Reports and Analytics
    Page<SmsLog> getSmsLogs(Pageable pageable);
    Page<SmsLog> getSmsLogsByEvent(Long eventId, Pageable pageable);
    SmsAnalyticsResponse getAnalytics(Long eventId);
    BalanceResponse getBalance() throws IOException, InterruptedException;
    
    // Delivery Reports
    DeliveryReportResponse getDeliveryReport(String campaignId) throws IOException, InterruptedException;
    
    // Message Preview
    String previewMessage(Long templateId, Map<String, String> variables);
}
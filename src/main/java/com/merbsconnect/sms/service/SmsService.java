package  com.merbsconnect.sms.service;


import com.merbsconnect.sms.dtos.request.BulkSmsRequest;
import com.merbsconnect.sms.dtos.request.CreateTemplateRequest;
import com.merbsconnect.sms.dtos.response.BulkSmsResponse;
import com.merbsconnect.sms.dtos.response.TemplateResponse;

import java.io.IOException;


public interface SmsService  {

    void sendSms();


    TemplateResponse createTemplate(CreateTemplateRequest request) throws IOException, InterruptedException;

    TemplateResponse getAllTemplates() throws IOException, InterruptedException;

    TemplateResponse getTemplateById(String templateId) throws IOException, InterruptedException;

    BulkSmsResponse sendBulkSms(BulkSmsRequest bulkSmsRequest);
}
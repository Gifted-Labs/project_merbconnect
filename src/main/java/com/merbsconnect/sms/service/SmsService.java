package com.merbsconnect.sms.service;


import com.merbsconnect.sms.dtos.request.CreateTemplateRequest;
import com.merbsconnect.sms.dtos.response.TemplateResponse;

import java.io.IOException;

public interface SmsService {

    TemplateResponse send(CreateTemplateRequest request) throws IOException, InterruptedException;

}

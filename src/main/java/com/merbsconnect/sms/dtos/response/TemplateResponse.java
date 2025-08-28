package com.merbsconnect.sms.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.merbsconnect.sms.model.Template;
import lombok.Data;

import java.util.List;

@Data
public class TemplateResponse {
    private String status;

    @JsonProperty("template_list")
    private List<Template> templateList;
}




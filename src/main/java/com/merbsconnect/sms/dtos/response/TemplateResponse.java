package com.merbsconnect.sms.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse {

    private String status;
    private String message;

    @JsonProperty("_id")
    private String _id;

    @JsonProperty("template_list")
    private List<TemplateData> template_list;

    @JsonProperty("template")
    private TemplateData template;

    @JsonProperty("templates")
    private List<TemplateData> templates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateData {
        @JsonProperty("_id")
        private Object _id;
        private String title;
        private String content;
        private String type;
    }

    public boolean isSuccessful() {
        return "success".equalsIgnoreCase(status);
    }

    public String getTemplateId() {
        return _id;
    }

    /**
     * Returns the template data, prioritizing templates over template_list and template.
     *
     * @return A list of TemplateData objects.
     */
    public List<TemplateData> getTemplates() {
        if (templates != null && !templates.isEmpty()) {
            return templates;
        } else if (template_list != null && !template_list.isEmpty()) {
            return template_list;
        } else if (template != null) {
            return List.of(template);
        } else {
            return List.of();
        }
    }
}

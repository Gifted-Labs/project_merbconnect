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
public class BulkSmsResponse {

    private String status;
    private String code;
    private String message;
    private SmsSummary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmsSummary {
        @JsonProperty("_id")
        private String id;

        @JsonProperty("type")
        private String type;

        @JsonProperty("total_sent")
        private Integer totalSent;

        private Integer contacts;

        @JsonProperty("total_rejected")
        private Integer totalRejected;

        @JsonProperty("numbers_sent")
        private List<String> numbersSent;

        @JsonProperty("credit_used")
        private Double creditUsed;

        @JsonProperty("credit_left")
        private Double creditLeft;
    }

    public boolean isSuccessful() {
        return "success".equalsIgnoreCase(status);
    }
}

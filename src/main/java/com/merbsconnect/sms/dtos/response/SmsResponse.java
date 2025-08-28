package com.merbsconnect.sms.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsResponse {

    private String status;
    private String code;
    private String message;

    @JsonProperty("summary")
    private Summary summaryTree;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        @JsonProperty("total_sent")
        private int totalSent;

        @JsonProperty("numbers_sent")
        private List<String> numbersSent;

        @JsonProperty("total_rejected")
        private int totalRejected;
    }
}

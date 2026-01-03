package com.merbsconnect.sms.model;

import lombok.Data;

@Data
public class Template {
    private Long _id;
    private String title;
    private String content;
    private String type;
}
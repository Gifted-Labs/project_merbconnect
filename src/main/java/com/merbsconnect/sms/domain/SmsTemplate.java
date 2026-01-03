package com.merbsconnect.sms.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sms_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Enumerated(EnumType.STRING)
    private SmsTemplateCategory category;
    
    @ElementCollection
    @CollectionTable(name = "sms_template_variables", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "variable_name")
    private List<String> variables;
    
    private String externalId; // mNotify template ID
    
    private boolean isActive = true;
    
    private boolean apiSyncFailed = false;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
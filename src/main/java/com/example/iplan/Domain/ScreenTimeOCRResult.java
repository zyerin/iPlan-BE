package com.example.iplan.Domain;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenTimeOCRResult {

    @DocumentId
    private String id;

    private String user_id;

    private LocalDate date;

    private Map<String, Object> result;
}

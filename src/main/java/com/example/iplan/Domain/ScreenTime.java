package com.example.iplan.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.firebase.database.annotations.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScreenTime {
    @DocumentId
    private String id;

    @NotNull
    private String user_id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String date;

    // 사용자가 정한 최소 측정 마감 시간
    @JsonFormat(pattern = "HH:mm")
    private String deadLineTime;

    // 사용자가 정한 핸드폰 사용 시간
    @JsonFormat(pattern = "HH:mm")
    private String goalTime;
}

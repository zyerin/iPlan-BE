package com.example.iplan.Domain;

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

    private LocalDate date;

    private LocalTime deadLineTime;

    private Duration goalTime;
}

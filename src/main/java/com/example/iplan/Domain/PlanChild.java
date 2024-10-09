package com.example.iplan.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.firebase.database.annotations.NotNull;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanChild {

    @DocumentId
    private String id; // Firestore 문서의 ID

    //User 테이블의 아이디
    @NotNull
    private String user_id;

    @NotNull
    private String title;
    private boolean alarm;

    //카테고리 테이블의 아이디
    private List<String> category_id;

    private LocalDate postDate;

    private String memo;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start_time;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end_time;
    private boolean is_completed;

}
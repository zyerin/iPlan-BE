package com.example.iplan.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.firebase.database.annotations.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @DocumentId
    private String id; // Firestore 문서의 ID

    @NotNull
    private String userId; // 아이의 고유 ID

    @NotNull
    private String content; // 보상의 내용

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String date; // 보상이 적용된 날짜

    private String planId;  // 이 보상이 어떤 계획과 연관되어 있는지 나타냄

    private boolean isRewarded; // 보상이 지급되었는지 여부
}

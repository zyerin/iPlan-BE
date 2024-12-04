package com.example.iplan.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.firebase.database.annotations.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardChild {

    @DocumentId
    private String id; // Firestore 문서의 ID (보상의 id)

    @NotNull
    private String user_id; // 아이의 고유 ID

    @NotNull
    private String content; // 보상의 내용

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String date; // 보상이 적용된 날짜

    @NotNull
    private String plan_id;  // 이 보상이 어떤 계획과 연관되어 있는지 나타냄

    private boolean rewarded; // 보상이 지급되었는지 여부

    private boolean success; // 보상이 지급 or 보류

}

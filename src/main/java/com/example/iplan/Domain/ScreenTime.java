package com.example.iplan.Domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.firebase.database.annotations.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenTime {

    @DocumentId
    private String id; // Firestore 문서의 ID

    @NotNull
    private String user_id; // 아이의 사용자 ID와 매핑

    @NotNull
    private int daily_limit; // 아이가 설정한 하루 제한 시간(분)

    private int used_time; // 아이가 사용한 시간(분)

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String start_time;  // 스크린타임 측정 시작 시간

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String date;  // 스크린타임 측정 날짜


}

package com.example.iplan.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardParentDTO {

    private String id; // Firestore 문서의 ID

    private String plan_id; // 연관된 계획의 ID

    private String user_id; // 부모님의 사용자 ID

    private String comment; // 부모님의 코멘트

    private int grade; // 별점 (예: 1~5 사이의 점수)

    private boolean is_rewarded; // 보상이 지급되었는지 여부
}

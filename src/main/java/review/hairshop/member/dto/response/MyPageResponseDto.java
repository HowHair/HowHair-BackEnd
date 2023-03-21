package review.hairshop.member.dto.response;

import lombok.*;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponseDto {
    private String nickname;
    private Gender gender;
    private LengthStatus lengthStatus;
    private CurlyStatus curlyStatus;
}

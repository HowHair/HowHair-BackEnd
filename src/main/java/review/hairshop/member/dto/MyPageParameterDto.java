package review.hairshop.member.dto;

import lombok.*;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageParameterDto {

    private Gender gender;
    private CurlyStatus curlyStatus;
}

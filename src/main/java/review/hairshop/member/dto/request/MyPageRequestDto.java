package review.hairshop.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;


import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageRequestDto {

    @NotNull(message = "성별은 필수 값 입니다.")
    private Gender gender;
    @NotNull(message = "곱슬 정도는 필수 값 입니다.")
    private CurlyStatus curlyStatus;
}

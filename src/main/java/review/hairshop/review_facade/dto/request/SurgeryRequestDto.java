package review.hairshop.review_facade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import review.hairshop.common.enums.surgery.SurgeryType;


import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryRequestDto {
    @NotNull(message = "시술 타입 종류는 필수 선택사항 입니다.")
    private SurgeryType surgeryType;
}

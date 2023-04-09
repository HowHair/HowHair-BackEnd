package review.hairshop.review_facade.dto.search;

import lombok.*;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.SurgeryDate;
import review.hairshop.common.enums.surgery.Dyeing;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DyeingSearchParameterDto {

    private Gender gender;
    private LengthStatus lengthStatus;
    private CurlyStatus curlyStatus;
    private SurgeryDate surgeryDate;
    private Dyeing dyeing;
    private int fromPrice;
    private int toPrice;
}

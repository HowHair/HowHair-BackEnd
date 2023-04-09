package review.hairshop.review_facade.dto.search;

import lombok.*;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.SurgeryDate;
import review.hairshop.common.enums.surgery.HairCut;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CutSearchParameterDto {
    private Gender gender;
    private LengthStatus lengthStatus;
    private CurlyStatus curlyStatus;
    private SurgeryDate surgeryDate;
    private HairCut hairCut;
    private int fromPrice;
    private int toPrice;
}

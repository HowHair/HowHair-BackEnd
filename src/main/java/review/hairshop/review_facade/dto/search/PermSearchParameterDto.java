package review.hairshop.review_facade.dto.search;

import lombok.*;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.SurgeryDate;
import review.hairshop.common.enums.surgery.HairCut;
import review.hairshop.common.enums.surgery.Perm;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermSearchParameterDto {
    private Gender gender;
    private LengthStatus lengthStatus;
    private CurlyStatus curlyStatus;
    private SurgeryDate surgeryDate;
    private Perm perm;
    private int fromPrice;
    private int toPrice;
}

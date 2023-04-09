package review.hairshop.review_facade.dto.request.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class PermSearchRequestDto {
    private Gender gender;
    private LengthStatus lengthStatus;
    private CurlyStatus curlyStatus;
    private SurgeryDate surgeryDate;
    private Perm perm;
    private int fromPrice;
    private int toPrice;
}

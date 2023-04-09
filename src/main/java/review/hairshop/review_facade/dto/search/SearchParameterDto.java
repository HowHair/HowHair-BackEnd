package review.hairshop.review_facade.dto.search;

import lombok.*;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.SurgeryDate;
import review.hairshop.common.enums.surgery.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchParameterDto {
    private SurgeryType surgeryType;

    private List<HairCut> hairCutList = new ArrayList<>();
    private List<Perm> permList = new ArrayList<>();
    private List<Dyeing> dyeingList = new ArrayList<>();
    private List<Straightening> straighteningList = new ArrayList<>();

    private Gender gender;
    private LengthStatus lengthStatus;
    private CurlyStatus curlyStatus;
    private SurgeryDate surgeryDate;
    private int fromPrice;
    private int toPrice;
}

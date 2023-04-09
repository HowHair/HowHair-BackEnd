package review.hairshop.review_facade.dto.request.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.SurgeryDate;
import review.hairshop.common.enums.surgery.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {
    @NotNull(message = "시술 종류는 필수 선택값 입니다.")
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

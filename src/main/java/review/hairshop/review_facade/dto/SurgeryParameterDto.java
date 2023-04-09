package review.hairshop.review_facade.dto;

import lombok.*;
import review.hairshop.common.enums.surgery.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryParameterDto {
    private HairCut hairCut;
    private Perm perm;
    private Dyeing dyeing;
    private Straightening straightening;

}

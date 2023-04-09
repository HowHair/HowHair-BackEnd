package review.hairshop.review_facade.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import review.hairshop.common.enums.surgery.Dyeing;
import review.hairshop.common.enums.surgery.HairCut;
import review.hairshop.common.enums.surgery.Perm;
import review.hairshop.common.enums.surgery.Straightening;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainSummaryDto {

    private Long reviewId;
    private HairCut hairCut;
    private Dyeing dyeing;
    private Perm perm;
    private Straightening straightening;
    private String imageUrl;

    @QueryProjection
    public MainSummaryDto(Long reviewId, HairCut hairCut, Dyeing dyeing, Perm perm, Straightening straightening) {
        this.reviewId = reviewId;
        this.hairCut = hairCut;
        this.dyeing = dyeing;
        this.perm = perm;
        this.straightening = straightening;
    }
}

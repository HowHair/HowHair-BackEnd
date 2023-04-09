package review.hairshop.review_facade.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import review.hairshop.common.enums.BookmarkYN;
import review.hairshop.common.enums.surgery.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSummaryDto {

    private Long reviewId;
    private int satisfaction;
    private String hairShopName;

    private HairCut hairCut;
    private Dyeing dyeing;
    private Perm perm;
    private Straightening straightening;

    private int numOfBookmark;
    private int price;
    private String imageUrl;


    @QueryProjection
    public ReviewSummaryDto(Long reviewId, int satisfaction,String hairShopName, HairCut hairCut, Dyeing dyeing, Perm perm, Straightening straightening, int price) {
        this.reviewId = reviewId;
        this.satisfaction = satisfaction;
        this.hairShopName = hairShopName;
        this.hairCut = hairCut;
        this.dyeing = dyeing;
        this.perm = perm;
        this.straightening = straightening;
        this.price = price;
    }
}

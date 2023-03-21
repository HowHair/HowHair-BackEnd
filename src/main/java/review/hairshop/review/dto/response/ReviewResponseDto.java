package review.hairshop.review.dto.response;

import lombok.*;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.RegYN;
import review.hairshop.common.enums.surgery.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private int satisfaction;

    private String hairShopName;
    private String hairShopNumber;
    private String hairShopAddress;
    private String designerName;

    private int price;
    private String content;
    private LengthStatus lengthStatus;
    private CurlyStatus curlyStatus;
    private LocalDate surgeryDate;

    private HairCut hairCut;
    private Dyeing dyeing;
    private Perm perm;
    private Straightening straightening;
    private OtherSurgery otherSurgery;
    private RegYN regYN;

    private List<String> imageUrlList;

}

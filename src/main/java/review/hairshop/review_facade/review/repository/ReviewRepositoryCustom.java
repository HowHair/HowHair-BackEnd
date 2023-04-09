package review.hairshop.review_facade.review.repository;

import org.springframework.data.domain.Pageable;
import review.hairshop.bookmark.Bookmark;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.SurgeryDate;
import review.hairshop.common.enums.surgery.*;
import review.hairshop.review_facade.dto.response.MainSummaryDto;
import review.hairshop.review_facade.dto.response.ReviewSummaryDto;
import review.hairshop.review_facade.review.Review;


import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryCustom {


    List<ReviewSummaryDto> searchReviewList(SurgeryType surgeryType, List<HairCut> hairCutList, List<Perm> permList, List<Dyeing> dyeingList, List<Straightening> straighteningList,
                                            Gender gender, LengthStatus lengthStatus, CurlyStatus curlyStatus, SurgeryDate surgeryDate,
                                            int fromPrice, int toPrice, Pageable pageable);


    List<ReviewSummaryDto> findMyReviewList(Long memberId, Pageable pageable);

    List<ReviewSummaryDto> findByHairShopName(String hairshopName, Pageable pageable);





}

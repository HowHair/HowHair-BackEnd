package review.hairshop.bookmark.repository;

import org.springframework.data.domain.Pageable;
import review.hairshop.bookmark.Bookmark;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.Status;
import review.hairshop.review_facade.dto.response.MainSummaryDto;
import review.hairshop.review_facade.dto.response.ReviewSummaryDto;

import java.util.List;

public interface BookmarkRepositoryCustom {

    List<ReviewSummaryDto> findBookmarkedReviewList(Long memberId, Pageable pageable);

    List<ReviewSummaryDto> findMostBookmarkedList(Pageable pageable);

    List<ReviewSummaryDto> findMostBookmarkedListAtMyType(Gender gender, LengthStatus lengthStatus, CurlyStatus curlyStatus, Pageable pageable);

    List<MainSummaryDto> findMainReviewList(Pageable pageable);

    List<MainSummaryDto> findMainTypeList(Gender gender, LengthStatus lengthStatus, CurlyStatus curlyStatus, Pageable pageable);



}

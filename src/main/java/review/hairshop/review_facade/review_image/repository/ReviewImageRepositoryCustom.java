package review.hairshop.review_facade.review_image.repository;

import review.hairshop.review_facade.review_image.ReviewImage;

import java.util.Optional;

public interface ReviewImageRepositoryCustom {

    Optional<ReviewImage> findFirstImageByReviewId(Long reviewId);

}

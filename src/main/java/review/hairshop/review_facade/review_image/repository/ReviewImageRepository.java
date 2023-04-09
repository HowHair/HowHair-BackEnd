package review.hairshop.review_facade.review_image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import review.hairshop.review_facade.review_image.ReviewImage;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> , ReviewImageRepositoryCustom {

    boolean existsByReviewId(Long reviewId);
}

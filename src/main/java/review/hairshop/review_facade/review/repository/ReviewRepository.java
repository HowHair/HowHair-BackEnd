package review.hairshop.review_facade.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import review.hairshop.common.enums.Status;
import review.hairshop.review_facade.review.Review;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> , ReviewRepositoryCustom{

    Optional<Review> findByIdAndStatus(Long id, Status status);
}

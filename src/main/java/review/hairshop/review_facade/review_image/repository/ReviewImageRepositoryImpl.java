package review.hairshop.review_facade.review_image.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.CollectionUtils;
import review.hairshop.review_facade.review_image.QReviewImage;
import review.hairshop.review_facade.review_image.ReviewImage;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static review.hairshop.review_facade.review_image.QReviewImage.reviewImage;

public class ReviewImageRepositoryImpl implements ReviewImageRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public ReviewImageRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<ReviewImage> findFirstImageByReviewId(Long reviewId) {

        ReviewImage reviewImage = queryFactory.selectFrom(QReviewImage.reviewImage)
                .where(QReviewImage.reviewImage.review.id.eq(reviewId))
                .orderBy(QReviewImage.reviewImage.createdAt.asc())
                .limit(1)
                .fetchOne();

        return reviewImage == null ? Optional.empty() : Optional.ofNullable(reviewImage);
    }
}

package review.hairshop.bookmark.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.springframework.data.domain.Pageable;
import review.hairshop.bookmark.Bookmark;
import review.hairshop.bookmark.QBookmark;
import review.hairshop.common.enums.CurlyStatus;
import review.hairshop.common.enums.Gender;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.Status;
import review.hairshop.review_facade.dto.response.MainSummaryDto;
import review.hairshop.review_facade.dto.response.QMainSummaryDto;
import review.hairshop.review_facade.dto.response.QReviewSummaryDto;
import review.hairshop.review_facade.dto.response.ReviewSummaryDto;

import javax.persistence.EntityManager;


import java.util.List;

import static review.hairshop.bookmark.QBookmark.bookmark;
import static review.hairshop.review_facade.review.QReview.review;

public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public BookmarkRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ReviewSummaryDto> findBookmarkedReviewList(Long memberId, Pageable pageable) {

        return queryFactory.select(new QReviewSummaryDto(review.id.as("reviewId"), review.satisfaction, review.hairShopName,
                        review.hairCut, review.dyeing, review.perm, review.straightening, review.price))
                .from(bookmark)
                .join(bookmark.review, review)
                .where(bookmark.member.id.eq(memberId), bookmark.status.eq(Status.ACTIVE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bookmark.createdAt.desc())
                .fetch();
    }

    /** ------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    public List<ReviewSummaryDto> findMostBookmarkedList(Pageable pageable) {
        return queryFactory.select(new QReviewSummaryDto(review.id.as("reviewId"), review.satisfaction, review.hairShopName,
                        review.hairCut, review.dyeing, review.perm, review.straightening, review.price))
                .from(bookmark)
                .join(bookmark.review, review)
                .groupBy(bookmark.review.id)
                .where(bookmark.status.eq(Status.ACTIVE), review.status.eq(Status.ACTIVE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bookmark.count().desc())
                .fetch();
    }

    /** ------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    public List<ReviewSummaryDto> findMostBookmarkedListAtMyType(Gender gender, LengthStatus lengthStatus, CurlyStatus curlyStatus, Pageable pageable) {
        return queryFactory.select(new QReviewSummaryDto(review.id.as("reviewId"), review.satisfaction, review.hairShopName,
                        review.hairCut, review.dyeing, review.perm, review.straightening, review.price))
                .from(bookmark)
                .join(bookmark.review, review).on(review.gender.eq(gender), review.lengthStatus.eq(lengthStatus), review.curlyStatus.eq(curlyStatus))
                .groupBy(bookmark.review.id)
                .where(bookmark.status.eq(Status.ACTIVE), review.status.eq(Status.ACTIVE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bookmark.count().desc())
                .fetch();
    }

    /** ------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    public List<MainSummaryDto> findMainReviewList(Pageable pageable) {
        return queryFactory.select(new QMainSummaryDto(review.id, review.hairCut, review.dyeing, review.perm, review.straightening))
                .from(bookmark)
                .join(bookmark.review, review)
                .groupBy(bookmark.review.id)
                .where(bookmark.status.eq(Status.ACTIVE), review.status.eq(Status.ACTIVE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bookmark.count().desc())
                .fetch();
    }

    /** ------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    public List<MainSummaryDto> findMainTypeList(Gender gender, LengthStatus lengthStatus, CurlyStatus curlyStatus, Pageable pageable) {
        return queryFactory.select(new QMainSummaryDto(review.id, review.hairCut, review.dyeing, review.perm, review.straightening))
                .from(bookmark)
                .join(bookmark.review, review).on(review.gender.eq(gender), review.lengthStatus.eq(lengthStatus), review.curlyStatus.eq(curlyStatus))
                .groupBy(bookmark.review.id)
                .where(bookmark.status.eq(Status.ACTIVE), review.status.eq(Status.ACTIVE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bookmark.count().desc())
                .fetch();
    }

    /** ------------------------------------------------------------------------------------------------------------------------------------------------------------*/



}

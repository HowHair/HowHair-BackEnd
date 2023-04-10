package review.hairshop.review_facade.review.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import review.hairshop.common.enums.*;
import review.hairshop.common.enums.surgery.*;
import review.hairshop.review_facade.dto.response.QReviewSummaryDto;
import review.hairshop.review_facade.dto.response.ReviewSummaryDto;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static review.hairshop.common.enums.surgery.SurgeryType.*;
import static review.hairshop.review_facade.review.QReview.review;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ReviewRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    /**
     * ------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    @Override
    public List<ReviewSummaryDto> searchReviewList(SurgeryType surgeryType, List<HairCut> hairCutList, List<Perm> permList, List<Dyeing> dyeingList, List<Straightening> straighteningList,
                                                   Gender gender, LengthStatus lengthStatus, CurlyStatus curlyStatus, SurgeryDate surgeryDate, int fromPrice, int toPrice, Pageable pageable) {

        return queryFactory.select(new QReviewSummaryDto(review.id.as("reviewId"), review.satisfaction, review.hairShopName,
                        review.hairCut, review.dyeing, review.perm, review.straightening, review.price))
                .from(review)
                .where(surgeryTypeCond(surgeryType), hairCutListIn(hairCutList), permListIn(permList), dyeingListIn(dyeingList), straighteningListIn(straighteningList),
                        review.status.eq(Status.ACTIVE), genderEq(gender), lengthStatusEq(lengthStatus), curlyStatusEq(curlyStatus), surgeryDateCond(surgeryDate), priceBetween(fromPrice, toPrice))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.createdAt.desc())
                .fetch();
    }

    private Predicate surgeryTypeCond(SurgeryType surgeryType) {
        if (surgeryType.equals(HAIR_CUT)) {
            return review.hairCut.ne(HairCut.NONE);
        } else if (surgeryType.equals(PERM)) {
            return review.perm.ne(Perm.NONE);
        } else if (surgeryType.equals(DYEING)) {
            return review.dyeing.ne(Dyeing.NONE);
        } else {
            return review.straightening.ne(Straightening.NONE);
        }
    }

    private Predicate hairCutListIn(List<HairCut> hairCutList) {
        return CollectionUtils.isEmpty(hairCutList) ? null : review.hairCut.in(hairCutList);
    }

    private Predicate permListIn(List<Perm> permList) {
        return CollectionUtils.isEmpty(permList) ? null : review.perm.in(permList);
    }

    private Predicate dyeingListIn(List<Dyeing> dyeingList) {
        return CollectionUtils.isEmpty(dyeingList) ? null : review.dyeing.in(dyeingList);
    }

    private Predicate straighteningListIn(List<Straightening> straighteningList) {
        return CollectionUtils.isEmpty(straighteningList) ? null : review.straightening.in(straighteningList);
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    private Predicate genderEq(Gender gender) {
        return gender != null ? review.gender.eq(gender) : null;
    }

    private Predicate lengthStatusEq(LengthStatus lengthStatus) {
        return lengthStatus != null ? review.lengthStatus.eq(lengthStatus) : null;
    }

    private Predicate curlyStatusEq(CurlyStatus curlyStatus) {
        return curlyStatus != null ? review.curlyStatus.eq(curlyStatus) : null;
    }

    private Predicate surgeryDateCond(SurgeryDate surgeryDate) {
        if (surgeryDate == null) {
            return null;
        }

        switch (surgeryDate) {
            case WITHIN_ONE_WEEKS:
                return review.surgeryDate.between(LocalDate.now().minusDays(7), LocalDate.now());

            case WITHIN_TWO_WEEKS:
                return review.surgeryDate.between(LocalDate.now().minusDays(14), LocalDate.now());

            default:
                return review.surgeryDate.before(LocalDate.now().minusDays(14));
        }
    }


    private Predicate priceBetween(int fromPrice, int toPrice) {
        if (fromPrice == 0 && toPrice == 0)
            return null;

        else if (fromPrice == 0)
            return review.price.loe(toPrice);

        else if (toPrice == 0)
            return review.price.goe(fromPrice);

        else
            return review.price.between(fromPrice, toPrice);
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    @Override
    public List<ReviewSummaryDto> findMyReviewList(Long memberId, Pageable pageable) {

        return queryFactory.select(new QReviewSummaryDto(review.id.as("reviewId"), review.satisfaction, review.hairShopName,
                        review.hairCut, review.dyeing, review.perm, review.straightening, review.price))
                .from(review)
                .where(review.member.id.eq(memberId), review.status.eq(Status.ACTIVE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.createdAt.desc())
                .fetch();
    }


    /**
     * ------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    @Override
    public List<ReviewSummaryDto> findByHairShopName(String hairshopName, Pageable pageable) {

        return queryFactory.select(new QReviewSummaryDto(review.id.as("reviewId"), review.satisfaction, review.hairShopName,
                        review.hairCut, review.dyeing, review.perm, review.straightening, review.price))
                .from(review)
                .where(review.hairShopName.eq(hairshopName), review.status.eq(Status.ACTIVE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.createdAt.desc())
                .fetch();
    }


}

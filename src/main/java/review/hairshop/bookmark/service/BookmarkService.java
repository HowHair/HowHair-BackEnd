package review.hairshop.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import review.hairshop.bookmark.Bookmark;
import review.hairshop.bookmark.repository.BookmarkRepository;
import review.hairshop.common.enums.Status;
import review.hairshop.common.response.ApiException;
import review.hairshop.member.Member;
import review.hairshop.member.repository.MemberRepository;
import review.hairshop.review_facade.review.Review;
import review.hairshop.review_facade.review.repository.ReviewRepository;


import static review.hairshop.common.enums.Status.ACTIVE;
import static review.hairshop.common.enums.Status.INACTIVE;
import static review.hairshop.common.response.ApiResponseStatus.INVALID_MEMBER;
import static review.hairshop.common.response.ApiResponseStatus.INVALID_REVIEW;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 이슈 : 기존에 북마크를 한 회원이 회원탈퇴를 해도 -> 기본 북마크는 남겨둔다 -> 이것이 기존 카카오스토리 서비스의 흐름
     * */
    @Transactional
    public void doBookmark(Long memberId, Long reviewId){

        //1. 북마크 조회
        Member findMember = getMember(memberId, ACTIVE);
        Review findReview = getReview(reviewId, ACTIVE);
        Bookmark bookmark = bookmarkRepository.findByMemberIdAndReviewId(memberId, reviewId).orElse(
                Bookmark.builder().member(findMember).review(findReview).status(INACTIVE).build()
        );

        //2.
        // 북마크를 새로 누른 경우에 -> 북마크가 추가되도록 - 단 리뷰 페이지에서 북마크가 증가되는 개수는 - 실제 총 데이터가 아닌 , 프론트 단의 데이터 - 그래야 동시에 다른사용자가 증가시켜도 혼란 없음
        // 북마크를 다시 누른 경우에 -> 북마크가 취소되도록 - 단 리뷰 페이지에서 북마크가 감소되는 게수는 - 실제 총 데이터가 아닌 , 프론트 단의 데이터 - 그랠야 동시에 다른 사용자가 감소시켜도 혼란 없음
        if(bookmark.getStatus().equals(INACTIVE)){
            bookmark.changeStatus(ACTIVE);
        } else{
            bookmark.changeStatus(INACTIVE);
        }

        //3. 실질적으로는 트랜젝션이 커밋된 후 - 플러시 되면서 - insert or update 쿼리가 나간다
        bookmarkRepository.save(bookmark); // 새로 생성된 북마크인 경우에는 insert 쿼리가 / 조회된 기존 북마크인 경우에는 update 쿼리가 나간다
    }


    /** [로그인 된 Member만을 가져오는 내부 서비스] */
    private Member getMember(Long memberId, Status status){
        return memberRepository.findByIdAndStatus(memberId, status).orElseThrow(
                () -> {
                    throw new ApiException(INVALID_MEMBER, "로그인 된 회원이 아닙니다.");
                }
        );
    }

    private Review getReview(Long reviewId, Status status){
        return reviewRepository.findByIdAndStatus(reviewId, status).orElseThrow(
                () -> {
                    throw new ApiException(INVALID_REVIEW, "유효한 리뷰가 아닙니다.");
                }
        );
    }
}

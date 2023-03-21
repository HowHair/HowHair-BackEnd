package review.hairshop.review_facade.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import review.hairshop.common.response.ApiResponse;
import review.hairshop.review_facade.dto.ReviewParameterDto;
import review.hairshop.review_facade.dto.request.ReviewRequestDto;
import review.hairshop.review_facade.dto.response.DeleteReviewResponseDto;
import review.hairshop.review_facade.dto.response.ReviewResponseDto;
import review.hairshop.review_facade.service.ReviewService;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;


    /**
     * [API 7.] : 리뷰 작성
     * */
    @PostMapping("/review")
    ApiResponse<ReviewResponseDto> postReview(@RequestAttribute Long memberId,
                                              @Validated @ModelAttribute ReviewRequestDto reviewRequestDto){

        ReviewParameterDto reviewParameterDto = ReviewParameterDto.builder()
                .hairShopName(reviewRequestDto.getHairShopName())
                .hairShopNumber(reviewRequestDto.getHairShopNumber())
                .hairShopAddress(reviewRequestDto.getHairShopAddress())
                .satisfaction(reviewRequestDto.getSatisfaction())
                .surgeryDate(reviewRequestDto.getSurgeryDate())
                .designerName(reviewRequestDto.getDesignerName())
                .hairCut(reviewRequestDto.getHairCut())
                .dyeing(reviewRequestDto.getDyeing())
                .perm(reviewRequestDto.getPerm())
                .straightening(reviewRequestDto.getStraightening())
                .otherSurgery(reviewRequestDto.getOtherSurgery())
                .lengthStatus(reviewRequestDto.getLengthStatus())
                .price(reviewRequestDto.getPrice())
                .content(reviewRequestDto.getContent())
                .imageList(reviewRequestDto.getImageList())
                .build();

        return ApiResponse.success(reviewService.register(memberId, reviewParameterDto));
    }

    /**
     * 8. [특정 Review 상세 조회]
     * */
    @GetMapping("/review/{reviewId}")
    public ApiResponse<ReviewResponseDto> getReview(@RequestAttribute Long memberId, @PathVariable Long reviewId){
        return ApiResponse.success(reviewService.getReview(memberId, reviewId));
    }

    /**
     * 9. [특정 Review 제거 -> 실질적으로는 INACTIVE하게 만듦]
     * */
    @PatchMapping("/review/{reviewId}")
    public ApiResponse<DeleteReviewResponseDto> patchReview(@RequestAttribute Long memberId, @PathVariable Long reviewId){
        return ApiResponse.success(reviewService.patchReview(memberId, reviewId));
    }

}

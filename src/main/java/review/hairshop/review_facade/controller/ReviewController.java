package review.hairshop.review_facade.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import review.hairshop.common.response.ApiResponse;
import review.hairshop.review_facade.dto.ReviewParameterDto;
import review.hairshop.review_facade.dto.request.HairShopRequestDto;
import review.hairshop.review_facade.dto.request.ReviewIdRequestDto;
import review.hairshop.review_facade.dto.request.ReviewRequestDto;
import review.hairshop.review_facade.dto.request.search.*;
import review.hairshop.review_facade.dto.response.DeleteReviewResponseDto;
import review.hairshop.review_facade.dto.response.MainSummaryDto;
import review.hairshop.review_facade.dto.response.ReviewResponseDto;
import review.hairshop.review_facade.dto.response.ReviewSummaryDto;
import review.hairshop.review_facade.dto.search.*;
import review.hairshop.review_facade.service.ReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;


    /**
     * [API 7.] : 리뷰 작성
     */
    @PostMapping("/review")
    ApiResponse<ReviewResponseDto> postReview(@RequestAttribute Long memberId,
                                              @Validated @ModelAttribute ReviewRequestDto reviewRequestDto) {

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
                .lengthStatus(reviewRequestDto.getLengthStatus())
                .price(reviewRequestDto.getPrice())
                .content(reviewRequestDto.getContent())
                .imageList(reviewRequestDto.getImageList())
                .build();

        return ApiResponse.success(reviewService.register(memberId, reviewParameterDto));
    }

    /**
     * API.8 [특정 Review 상세 조회]
     */
    @GetMapping("/review/{reviewId}")
    public ApiResponse<ReviewResponseDto> getReview(@RequestAttribute Long memberId, @PathVariable Long reviewId) {
        return ApiResponse.success(reviewService.getReview(memberId, reviewId));
    }

    /**
     * API.9 [특정 Review 제거 -> 실질적으로는 INACTIVE하게 만듦]
     */
    @PatchMapping("/review")
    public ApiResponse<DeleteReviewResponseDto> patchReview(@RequestAttribute Long memberId, @RequestBody ReviewIdRequestDto reviewIdRequestDto) {
        return ApiResponse.success(reviewService.patchReview(memberId, reviewIdRequestDto.getReviewId()));
    }


    /**
     * API.11 [특정 시술에 따른 리뷰 리스트들 중에서 -> 세부 필터에 따른 검색]
     * */

    @GetMapping("/review/list")
    public ApiResponse<List<ReviewSummaryDto>> searchReviewList(@Validated @ModelAttribute SearchRequestDto searchRequestDto, Pageable pageable){

        SearchParameterDto searchParameterDto = SearchParameterDto.builder()
                .surgeryType(searchRequestDto.getSurgeryType())
                .hairCutList(searchRequestDto.getHairCutList())
                .dyeingList(searchRequestDto.getDyeingList())
                .permList(searchRequestDto.getPermList())
                .straighteningList(searchRequestDto.getStraighteningList())
                .gender(searchRequestDto.getGender())
                .lengthStatus(searchRequestDto.getLengthStatus())
                .curlyStatus(searchRequestDto.getCurlyStatus())
                .surgeryDate(searchRequestDto.getSurgeryDate())
                .fromPrice(searchRequestDto.getFromPrice())
                .toPrice(searchRequestDto.getToPrice())
                .build();

        return ApiResponse.success(reviewService.searchReviewList(searchParameterDto, pageable));
    }



    /**
     * API.12 [내가 쓴 리뷰 리스트 조회]
     * */
    @GetMapping("/my/review/list")
    public ApiResponse<List<ReviewSummaryDto>> geyMyReviewList(@RequestAttribute Long memberId, Pageable pageable){
        return ApiResponse.success(reviewService.getMyReviewList(memberId, pageable));
     }

     /**
      * API.13 [북마크 한 리뷰 리스트 조회]
      * */
     @GetMapping("/my/bookmark/list")
    public ApiResponse<List<ReviewSummaryDto>> geyBookmarkReviewList(@RequestAttribute Long memberId, Pageable pageable){
         return ApiResponse.success(reviewService.getBookmarkReviewList(memberId, pageable));
     }

     /**
      * API.14 [미용실 리스트에서 , 특정 미용실을 클릭했을 떄 -> 그 미용실의 리뷰 리스트를 조회]
      * */
     @GetMapping("/hairshop/review/list")
    public ApiResponse<List<ReviewSummaryDto>> getReviewListByHairShop(@ModelAttribute HairShopRequestDto hairShopRequestDto, Pageable pageable){
         return ApiResponse.success(reviewService.getReviewListByHairShop(hairShopRequestDto.getHairshopName(), pageable));
     }

     /**
      * API. 15 [북마크 개수에 따라 -> 인기 많은 ReviewList 조회]
      * */
     @GetMapping("/recommended/review/list")
    public ApiResponse<List<ReviewSummaryDto>> getRecommendedReviewList(Pageable pageable){
         return ApiResponse.success(reviewService.getRecommendedReviewList(pageable));
     }

     /**
      * API.16 [북마크 개수에 따라 -> 인기 많은 ReviewList를 조회하되 , 설정한 MyType에 맞는 리뷰만 조회]
      * */
     @GetMapping("/recommend/mytype/review/list")
    public ApiResponse<List<ReviewSummaryDto>> getRecommendedMyTypeList(@RequestAttribute Long memberId, Pageable pageable){
         return ApiResponse.success(reviewService.getRecommendedMyTypeList(memberId, pageable));
     }

     /**
      * API17.1 [메인화면에서 -> 내 취향 스타일]
      * */
    @GetMapping("/main/review/list")
    public ApiResponse<List<MainSummaryDto>> getMainReviewList(Pageable pageable){
        return ApiResponse.success(reviewService.getMainReviewList(pageable));
    }

    /**
     * API17.2 [메인화면에서 -> 인기 많은 스타일]
     * */
    @GetMapping("/main/mytype/review/list")
    public ApiResponse<List<MainSummaryDto>> getMainMyTypeList(@RequestAttribute Long memberId, Pageable pageable){
        return ApiResponse.success(reviewService.getMainMyTypeList(memberId, pageable));
    }


}

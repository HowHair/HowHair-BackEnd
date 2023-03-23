package review.hairshop.review_facade.service;


import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import review.hairshop.common.enums.Status;
import review.hairshop.common.response.ApiException;
import review.hairshop.common.utils.FileServiceUtil;
import review.hairshop.member.Member;
import review.hairshop.member.repository.MemberRepository;
import review.hairshop.review_facade.review.Review;
import review.hairshop.review_facade.dto.ReviewParameterDto;
import review.hairshop.review_facade.dto.response.DeleteReviewResponseDto;
import review.hairshop.review_facade.dto.response.ReviewResponseDto;
import review.hairshop.review_facade.review.repository.ReviewRepository;
import review.hairshop.review_facade.review_image.ReviewImage;
import review.hairshop.review_facade.review_image.repository.ReviewImageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static review.hairshop.common.enums.RegYN.N;
import static review.hairshop.common.enums.RegYN.Y;
import static review.hairshop.common.enums.Status.ACTIVE;
import static review.hairshop.common.enums.Status.INACTIVE;
import static review.hairshop.common.response.ApiResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final MemberRepository memberRepository;
    private final FileServiceUtil fileServiceUtil;


    @Transactional
    public ReviewResponseDto register(Long memberId, ReviewParameterDto reviewParameterDto){

        //0. 이미지 파일 형식에 속하는지 확인
        if(!fileServiceUtil.isAllImageExt(reviewParameterDto.getImageList())){
            throw new ApiException(INVALID_EXTENSION, "지원하지 않는 이미지 파일 형식 입니다.");
        }

        Member findMember = getMember(memberId);

        //1. Review 엔티티 생성하여 저장
        Review review = createReview(findMember, reviewParameterDto);
        reviewRepository.save(review);

        /** 만약 저장할 함께 넘어온 이미지가 하나도 없다면 그대로 리턴 */
        if(CollectionUtils.isEmpty(reviewParameterDto.getImageList())){
            List<String> sampleUrlList = fileServiceUtil.getSampleUrlList();
            return createReviewResponse(memberId, review, sampleUrlList);
        }

        //2. ReviewImage 엔티티 생성하여 저장
        List<String> pathList = fileServiceUtil.getPathList(reviewParameterDto, review.getId());
        List<ReviewImage> reviewImageList = pathList.stream()
                                                    .map(p -> ReviewImage.builder().path(p).review(review).status(ACTIVE).build())
                                                    .collect(Collectors.toList());
        reviewImageRepository.saveAll(reviewImageList);

        //3. 실제 Review Image 파일 저장
        createList(0, pathList.size()).forEach(
                idx -> fileServiceUtil.uploadImage(pathList.get(idx), reviewParameterDto.getImageList().get(idx))
        );

        //4. 이후 실제 경로를 암호화 시킨 후 , ReviewResponseDto를 만들어서 반환
        List<String> imageUrlList = fileServiceUtil.getImageUrlList(pathList);
        return createReviewResponse(memberId, review, imageUrlList);
    }

    /** [로그인 된 Member만을 가져오는 내부 서비스] */
    private Member getMember(Long memberId){
        return memberRepository.findByIdAndStatus(memberId, ACTIVE).orElseThrow(
                () -> {
                    throw new ApiException(INVALID_MEMBER, "로그인 된 회원이 아닙니다.");
                }
        );
    }

    private List<Integer> createList(int from, int to){
        List<Integer> list = new ArrayList<>();
        for(int i=from; i<to; i++){
            list.add(i);
        }

        return list;
    }

    /** [선택한 값에 따라 Review 엔티티를 생성하는 내부 서비스] */
    private Review createReview(Member member, ReviewParameterDto reviewParameterDto){

        return Review.builder()
                .satisfaction(reviewParameterDto.getSatisfaction())
                .hairShopName(reviewParameterDto.getHairShopName())
                .hairShopNumber(reviewParameterDto.getHairShopNumber())
                .hairShopAddress(reviewParameterDto.getHairShopAddress())
                .designerName(reviewParameterDto.getDesignerName())
                .lengthStatus(reviewParameterDto.getLengthStatus())
                .curlyStatus(member.getCurlyStatus())
                .price(reviewParameterDto.getPrice())
                .content(reviewParameterDto.getContent())
                .surgeryDate(reviewParameterDto.getSurgeryDate())
                .status(ACTIVE)
                .hairCut(reviewParameterDto.getHairCut())
                .dyeing(reviewParameterDto.getDyeing())
                .perm(reviewParameterDto.getPerm())
                .straightening(reviewParameterDto.getStraightening())
                .otherSurgery(reviewParameterDto.getOtherSurgery())
                .member(member)
                .build();
    }


    /** [선택한 값에 따른 ReviewResponseDto를 생성하는 내부 서비스] : 넘어온 이미지가 있는 경우는*/
    private ReviewResponseDto createReviewResponse(Long memberId, Review review, List<String> imageUrlList){
        return ReviewResponseDto.builder()
                .reviewId(review.getId())
                .satisfaction(review.getSatisfaction())
                .hairShopName(review.getHairShopName())
                .hairShopNumber(review.getHairShopNumber())
                .hairShopAddress(review.getHairShopAddress())
                .designerName(review.getDesignerName())
                .price(review.getPrice())
                .content(review.getContent())
                .surgeryDate(review.getSurgeryDate())
                .lengthStatus(review.getLengthStatus())
                .curlyStatus(review.getCurlyStatus())
                .hairCut(review.getHairCut())
                .dyeing(review.getDyeing())
                .perm(review.getPerm())
                .straightening(review.getStraightening())
                .otherSurgery(review.getOtherSurgery())
                .regYN(review.getMember().getId().equals(memberId) ? Y : N)
                .numOfBookmark(CollectionUtils.isEmpty(review.getBookmarkList()) ? 0 : review.getBookmarkList().size())
                .imageUrlList(imageUrlList)
                .build();
    }

    /**-------------------------------------------------------------------------------------------------------------------------------------------- */
    public ReviewResponseDto getReview(Long memberId, Long reviewId){

        //1. ACTIVE한 리뷰를 조회
        Review findReview = getReview(reviewId, ACTIVE);

        //2. 조회한 리뷰에 함꼐 등록된 사진이 1개라도 있는지의 여부에 따라 적절한 ReviewResponse를 생성하여 리턴
        if(CollectionUtils.isEmpty(findReview.getReviewImageList())){
            List<String> sampleUrlList = fileServiceUtil.getSampleUrlList();
            return createReviewResponse(memberId, findReview, sampleUrlList);
        }

        List<String> pathList = findReview.getReviewImageList().stream()
                .map(ri -> ri.getPath())
                .collect(Collectors.toList());

        List<String> imageUrlList = fileServiceUtil.getImageUrlList(pathList);

        return createReviewResponse(memberId, findReview, imageUrlList);
    }

    private Review getReview(Long reviewId, Status status){
        return reviewRepository.findByIdAndStatus(reviewId, status).orElseThrow(
                () -> {
                    throw new ApiException(INVALID_REVIEW, "유효한 리뷰가 아닙니다.");
                }
        );
    }



    /** ---------------------------------------------------------------------------------------------------------------------- */
    @Transactional
    public DeleteReviewResponseDto patchReview(Long memberId, Long reviewId){


        //0. ACTIVE 한 리뷰를 조회하여 , 그 리뷰의 작성자가 해당 요청을 보낸 member인지를 검사
        Review findReview = getReview(reviewId, ACTIVE);
        if(!findReview.getMember().getId().equals(memberId)){
            throw new ApiException(INVALID_MEMBER_AT_REVIEW_DELETE, "해당 회원이 쓴 리뷰가 아니기 때문에, 해당 회원은 이 리뷰를 지울 수 없습니다.");
        }

        //1. 리뷰만을 INACTIVE 하게 change -> 이때 ReviewImage는 어차피 Review에 종속적인 엔티티니깐 , Review만 INACTIVE하게 만들면 논리적으로 ReivewImage도 inactive하게 됨
        findReview.changeStatus(INACTIVE);

        //2. 응답 리턴
        return DeleteReviewResponseDto.builder()
                                      .reviewId(findReview.getId())
                                      .build();
    }

}

package review.hairshop.review_facade.service;


import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import review.hairshop.bookmark.Bookmark;
import review.hairshop.bookmark.repository.BookmarkRepository;
import review.hairshop.common.enums.BookmarkYN;
import review.hairshop.common.enums.RegYN;
import review.hairshop.common.enums.Status;
import review.hairshop.common.enums.surgery.SurgeryType;
import review.hairshop.common.response.ApiException;
import review.hairshop.common.utils.FileServiceUtil;
import review.hairshop.member.Member;
import review.hairshop.member.repository.MemberRepository;
import review.hairshop.review_facade.dto.response.MainSummaryDto;
import review.hairshop.review_facade.dto.response.ReviewSummaryDto;
import review.hairshop.review_facade.dto.search.*;
import review.hairshop.review_facade.review.Review;
import review.hairshop.review_facade.dto.ReviewParameterDto;
import review.hairshop.review_facade.dto.response.DeleteReviewResponseDto;
import review.hairshop.review_facade.dto.response.ReviewResponseDto;
import review.hairshop.review_facade.review.repository.ReviewRepository;
import review.hairshop.review_facade.review_image.ReviewImage;
import review.hairshop.review_facade.review_image.repository.ReviewImageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static review.hairshop.common.enums.BookmarkYN.*;
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
    private final BookmarkRepository bookmarkRepository;


    @Transactional
    public ReviewResponseDto register(Long memberId, ReviewParameterDto reviewParameterDto){

        //0. 이미지 파일 형식에 속하는지 확인
        if(!fileServiceUtil.isAllImageExt(reviewParameterDto.getImageList())){
            throw new ApiException(INVALID_EXTENSION, "지원하지 않는 이미지 파일 형식 입니다.");
        }

        Member findMember = getMember(memberId, ACTIVE);

        //1. Review 엔티티 생성하여 저장
        Review review = createReview(findMember, reviewParameterDto);
        reviewRepository.save(review);

        /** 만약 저장할 함께 넘어온 이미지가 하나도 없다면 그대로 리턴 */
        if(CollectionUtils.isEmpty(reviewParameterDto.getImageList())){
            List<String> sampleUrlList = fileServiceUtil.getSampleUrlList();
            return createReviewResponse(findMember, review, sampleUrlList);
        }

        //2. ReviewImage 엔티티 생성하여 저장
        List<String> pathList = fileServiceUtil.getPathList(reviewParameterDto.getImageList(), review.getId());
        List<ReviewImage> reviewImageList = pathList.stream()
                                                    .map(p -> ReviewImage.builder().path(p).review(review).status(ACTIVE).build())
                                                    .collect(Collectors.toList());
        reviewImageRepository.saveAll(reviewImageList);

        //3. 실제 Review Image 파일 저장
        IntStream.range(0, pathList.size()).forEach(
                idx -> fileServiceUtil.uploadImage(pathList.get(idx), reviewParameterDto.getImageList().get(idx))
        );

        //4. 이후 실제 경로를 암호화 시킨 후 , ReviewResponseDto를 만들어서 반환
        List<String> imageUrlList = fileServiceUtil.getImageUrlList(pathList);
        return createReviewResponse(findMember, review, imageUrlList);
    }

    /** [로그인 된 Member만을 가져오는 내부 서비스] */
    private Member getMember(Long memberId, Status status){
        return memberRepository.findByIdAndStatus(memberId, status).orElseThrow(
                () -> {
                    throw new ApiException(INVALID_MEMBER, "로그인 된 회원이 아닙니다.");
                }
        );
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
                .gender(member.getGender())
                .curlyStatus(member.getCurlyStatus())
                .price(reviewParameterDto.getPrice())
                .content(reviewParameterDto.getContent())
                .surgeryDate(reviewParameterDto.getSurgeryDate())
                .status(ACTIVE)
                .hairCut(reviewParameterDto.getHairCut())
                .dyeing(reviewParameterDto.getDyeing())
                .perm(reviewParameterDto.getPerm())
                .straightening(reviewParameterDto.getStraightening())
                .member(member)
                .build();
    }


    /** [선택한 값에 따른 ReviewResponseDto를 생성하는 내부 서비스] : 넘어온 이미지가 있는 경우는*/
    private ReviewResponseDto createReviewResponse(Member member, Review review, List<String> imageUrlList){
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
                .memberName(member.getName())
                .gender(member.getGender())
                .regYN(review.getMember().getId().equals(member.getId()) ? Y : RegYN.N)
                .bookmarkYN(BookmarkYN.N)
                .numOfBookmark(0)
                .imageUrlList(imageUrlList)
                .build();
    }


    /**-------------------------------------------------------------------------------------------------------------------------------------------- */
    public ReviewResponseDto getReview(Long memberId, Long reviewId){

        //1. ACTIVE한 리뷰를 조회
        Review findReview = getReview(reviewId, ACTIVE);
        Member findMember = getMember(memberId, ACTIVE);

        // 북마크 여부와 , 북마크 개수를 가져옴
        boolean isBookmarked = bookmarkRepository.existsByMemberIdAndReviewIdAndStatus(memberId, reviewId, ACTIVE);
        long numOfBookmark = bookmarkRepository.findCountByReviewIdAndStatus(reviewId, ACTIVE);

        //2. 조회한 리뷰에 함꼐 등록된 사진이 1개라도 있는지의 여부에 따라 적절한 ReviewResponse를 생성하여 리턴
        if(CollectionUtils.isEmpty(findReview.getReviewImageList())){
            List<String> sampleUrlList = fileServiceUtil.getSampleUrlList();
            return createReviewResponse(findMember, findReview, sampleUrlList, isBookmarked, (int)numOfBookmark);
        }

        List<String> pathList = findReview.getReviewImageList().stream()
                .map(ri -> ri.getPath())
                .collect(Collectors.toList());

        List<String> imageUrlList = fileServiceUtil.getImageUrlList(pathList);


        return createReviewResponse(findMember, findReview, imageUrlList, isBookmarked, (int)numOfBookmark);
    }

    private Review getReview(Long reviewId, Status status){
        return reviewRepository.findByIdAndStatus(reviewId, status).orElseThrow(
                () -> {
                    throw new ApiException(INVALID_REVIEW, "유효한 리뷰가 아닙니다.");
                }
        );
    }

    private ReviewResponseDto createReviewResponse(Member member, Review review, List<String> imageUrlList, boolean isBookmarked, int numOfBookmark){
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
                .memberName(member.getName())
                .gender(review.getGender())
                .regYN(review.getMember().getId().equals(member.getId()) ? Y : RegYN.N)
                .bookmarkYN(isBookmarked ? BookmarkYN.Y : BookmarkYN.N)
                .numOfBookmark(numOfBookmark)
                .imageUrlList(imageUrlList)
                .build();
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

    /** ---------------------------------------------------------------------------------------------------------------------- */



    /** ---------------------------------------------------------------------------------------------------------------------- */
    public List<ReviewSummaryDto> searchReviewList(SearchParameterDto searchParameterDto, Pageable pageable){

        //1. imageUrl을 제외한 , searchParameterDto에 있는 조건에 따라 ReviewSummaryDtoList 로 바로 조회
        List<ReviewSummaryDto> reviewSummaryDtoList = reviewRepository.searchReviewList(searchParameterDto.getSurgeryType(), searchParameterDto.getHairCutList(), searchParameterDto.getPermList(), searchParameterDto.getDyeingList(), searchParameterDto.getStraighteningList(),
                searchParameterDto.getGender(), searchParameterDto.getLengthStatus(), searchParameterDto.getCurlyStatus(), searchParameterDto.getSurgeryDate(), searchParameterDto.getFromPrice(), searchParameterDto.getToPrice(),
                pageable);

        //1_2. 단 ReveiwSummaryDto 자체가 하나도 없다면 빈 리스트를 리턴
        if(CollectionUtils.isEmpty(reviewSummaryDtoList)){
            return reviewSummaryDtoList;
        }

        //2. 북마크 개수를 count해서 dto에 setting
        IntStream.range(0, reviewSummaryDtoList.size())
                .forEach(idx -> reviewSummaryDtoList.get(idx).setNumOfBookmark((int)bookmarkRepository.findCountByReviewIdAndStatus(reviewSummaryDtoList.get(idx).getReviewId(), ACTIVE)));

       //3. 이후 그 id값들을 가지고 imageUrl을 만들어서 setting
        /** 일단 첫 이미지 url들을 리스트로 정리한 후*/
        List<String> firstImageUrlList = reviewSummaryDtoList.stream()
                .map(dto -> reviewImageRepository.findFirstImageByReviewId(dto.getReviewId()))
                .map(optionalReviewImage -> getFirstImageUrl(optionalReviewImage))
                .collect(Collectors.toList());

        /** 각 서머리 dto에 , 그 이미지 url들을 setting 해줌 */
        IntStream.range(0, reviewSummaryDtoList.size())
                .forEach(idx -> reviewSummaryDtoList.get(idx).setImageUrl(firstImageUrlList.get(idx)));

        //4. 리턴
        return reviewSummaryDtoList;
    }

    /**
     * [특정 리뷰와 관련된 첫번째 리뷰 이미지를 Optional로 감싸서 -> 그 감싼 값이 인자로 넘어갔을 때]
     * -> 인자값이 있다면 == 그 리뷰와 관련된 리뷰 이미지가 하나라도 있다면 - 그 리뷰이미지 안에 있는 path를 가지고 url을 만들어 리턴하고
     * -> 인자값이 없다면 == 그 리뷰와 관련된 리뷰 이미지가 하나라도 없다면 - 샘플 이미지 url 을 리턴
     * */

    private String getFirstImageUrl(Optional<ReviewImage> optionalReviewImage){
        String imageUrl;

        if(optionalReviewImage.isPresent()){
            String firstImagePath = optionalReviewImage.get().getPath();
            List<String> imagePathList = List.of(firstImagePath);
            imageUrl = fileServiceUtil.getImageUrlList(imagePathList).get(0);
        } else{
            imageUrl = fileServiceUtil.getSampleUrlList().get(0);
        }

        return imageUrl;
    }

    /** ---------------------------------------------------------------------------------------------------------------------- */



    /**
     * [내가 쓴 리뷰 리스트를 조회해오는 서비스]
    * */
    public List<ReviewSummaryDto> getMyReviewList(Long memberId, Pageable pageable){

        //1. imageUrl을 제외한 , memberId가 일치하는 조건에 따라 ReviewSummaryDtoList 로 바로 조회
        List<ReviewSummaryDto> myReviewList = reviewRepository.findMyReviewList(memberId, pageable);

        //1_2. 단 ReveiwSummaryDto 자체가 하나도 없다면 빈 리스트를 리턴
        if (CollectionUtils.isEmpty(myReviewList)) {
            return myReviewList;
        }

        //2. 북마크 개수를 count해서 dto에 setting
        IntStream.range(0, myReviewList.size())
                .forEach(idx -> myReviewList.get(idx).setNumOfBookmark((int)bookmarkRepository.findCountByReviewIdAndStatus(myReviewList.get(idx).getReviewId(), ACTIVE)));


        //3. 이후 그 id값들을 가지고 imageUrl을 만들어서 setting
        /** 일단 첫 이미지 url들을 리스트로 정리한 후*/
        List<String> firstImageUrlList = myReviewList.stream()
                .map(dto -> reviewImageRepository.findFirstImageByReviewId(dto.getReviewId()))
                .map(optionalReviewImage -> getFirstImageUrl(optionalReviewImage))
                .collect(Collectors.toList());

        /** 각 서머리 dto에 , 그 이미지 url들을 setting 해줌 */
        IntStream.range(0, myReviewList.size())
                .forEach(idx -> myReviewList.get(idx).setImageUrl(firstImageUrlList.get(idx)));

        //4. 리턴
        return myReviewList;
    }

    /** ---------------------------------------------------------------------------------------------------------------------- */

    /**
     * [내가 북마크 한 리뷰 리스트 조회 서비스]
     * */

    public List<ReviewSummaryDto> getBookmarkReviewList(Long memberId, Pageable pageable){

        //1. 내가 북마크 한 , 북마크 리스트 조회
        List<ReviewSummaryDto> bookmarkedReviewList = bookmarkRepository.findBookmarkedReviewList(memberId, pageable);

        //1_2. 북마크 한 리뷰가 없다면 -> 그대로 빈 배열 리턴
        if(CollectionUtils.isEmpty(bookmarkedReviewList)){
            return bookmarkedReviewList;
        }

        //2. 북마크 개수를 count해서 dto에 setting
        IntStream.range(0, bookmarkedReviewList.size())
                .forEach(idx -> bookmarkedReviewList.get(idx).setNumOfBookmark((int)bookmarkRepository.findCountByReviewIdAndStatus(bookmarkedReviewList.get(idx).getReviewId(), ACTIVE)));

        //3. 이후 그 id값들을 가지고 imageUrl을 만들어서 setting
        /** 일단 첫 이미지 url들을 리스트로 정리한 후*/
        List<String> firstImageUrlList = bookmarkedReviewList.stream()
                .map(dto -> reviewImageRepository.findFirstImageByReviewId(dto.getReviewId()))
                .map(optionalReviewImage -> getFirstImageUrl(optionalReviewImage))
                .collect(Collectors.toList());

        /** 각 서머리 dto에 , 그 이미지 url들을 setting 해줌 */
        IntStream.range(0, bookmarkedReviewList.size())
                .forEach(idx -> bookmarkedReviewList.get(idx).setImageUrl(firstImageUrlList.get(idx)));

        //4. 리턴
        return bookmarkedReviewList;
    }

    /** ---------------------------------------------------------------------------------------------------------------------- */

    /**
     * [특정 미용실의 리뷰 리스트를 조회해오는 서비스]
     * */

    public List<ReviewSummaryDto> getReviewListByHairShop(String hairshopName, Pageable pageable){

        //1. 선택한 미용실 이름에 따른 리뷰 서머리 리스트 조회
        List<ReviewSummaryDto> reviewSummaryDtoList = reviewRepository.findByHairShopName(hairshopName, pageable);

        //1_2. 빈 리스트일 경우 -> 빈 리스트 그대로 리턴
        if(CollectionUtils.isEmpty(reviewSummaryDtoList)){
            return reviewSummaryDtoList;
        }

        //2. 북마크 개수를 count해서 dto에 setting
        IntStream.range(0, reviewSummaryDtoList.size())
                .forEach(idx -> reviewSummaryDtoList.get(idx).setNumOfBookmark((int)bookmarkRepository.findCountByReviewIdAndStatus(reviewSummaryDtoList.get(idx).getReviewId(), ACTIVE)));


        //3. 이후 그 id값들을 가지고 imageUrl을 만들어서 setting
        /** 일단 첫 이미지 url들을 리스트로 정리한 후*/
        List<String> firstImageUrlList = reviewSummaryDtoList.stream()
                .map(dto -> reviewImageRepository.findFirstImageByReviewId(dto.getReviewId()))
                .map(optionalReviewImage -> getFirstImageUrl(optionalReviewImage))
                .collect(Collectors.toList());

        /** 각 서머리 dto에 , 그 이미지 url들을 setting 해줌 */
        IntStream.range(0, reviewSummaryDtoList.size())
                .forEach(idx -> reviewSummaryDtoList.get(idx).setImageUrl(firstImageUrlList.get(idx)));

        //4. 리턴
        return reviewSummaryDtoList;
    }

    /** ---------------------------------------------------------------------------------------------------------------------- */

    /**
     * [북마크가 많은 순서대로 ReviewSummaryDto를 조회하는 서비스]
     * */
    public List<ReviewSummaryDto> getRecommendedReviewList(Pageable pageable){

        //1. reviewId를 기준으로 북마크를 groupBy한 후 - 그 count 값에 따라 북마크를 내림창순 정렬해서 - review와 join 하여 가져옴 -> 그게 ReviewSummaryDto
        List<ReviewSummaryDto> mostBookmarkedList = bookmarkRepository.findMostBookmarkedList(pageable);

        //1_2. 만약 리스트가 비었다면 그대로 리턴
        if(CollectionUtils.isEmpty(mostBookmarkedList)){
            return mostBookmarkedList;
        }

        //2. 북마크 개수를 count해서 dto에 setting
        IntStream.range(0, mostBookmarkedList.size())
                .forEach(idx -> mostBookmarkedList.get(idx).setNumOfBookmark((int)bookmarkRepository.findCountByReviewIdAndStatus(mostBookmarkedList.get(idx).getReviewId(), ACTIVE)));


        //3. 이후 그 id값들을 가지고 imageUrl을 만들어서 setting
        /** 일단 첫 이미지 url들을 리스트로 정리한 후*/
        List<String> firstImageUrlList = mostBookmarkedList.stream()
                .map(dto -> reviewImageRepository.findFirstImageByReviewId(dto.getReviewId()))
                .map(optionalReviewImage -> getFirstImageUrl(optionalReviewImage))
                .collect(Collectors.toList());

        /** 각 서머리 dto에 , 그 이미지 url들을 setting 해줌 */
        IntStream.range(0, mostBookmarkedList.size())
                .forEach(idx -> mostBookmarkedList.get(idx).setImageUrl(firstImageUrlList.get(idx)));

        //4. 리턴
        return mostBookmarkedList;
    }

    /** ---------------------------------------------------------------------------------------------------------------------- */

    /**
     * [북마크가 많은 순서대로 ReviewSummaryDto를 조회하는 서비스] + [단 그중 미리 설정한 MyType에 맞는 리뷰만 보여줌!]
     * */
    public List<ReviewSummaryDto> getRecommendedMyTypeList(Long memberId, Pageable pageable){

        //0. Member 조회
        Member findMember = getMember(memberId, ACTIVE);

        //1. reviewId를 기준으로 북마크를 groupBy한 후 - 그 count 값에 따라 북마크를 내림창순 정렬해서 가져옴 -> 이때 MyType에 맞는 Review하고만 join 하여 가져옴
        List<ReviewSummaryDto> mostBookmarkedListAtMyType = bookmarkRepository.findMostBookmarkedListAtMyType(findMember.getGender(), findMember.getLengthStatus(), findMember.getCurlyStatus(), pageable);

        //1_2. 만약 리스트가 비었다면 그대로 리턴
        if(CollectionUtils.isEmpty(mostBookmarkedListAtMyType)){
            return mostBookmarkedListAtMyType;
        }

        //2. 북마크 개수를 count해서 dto에 setting
        IntStream.range(0, mostBookmarkedListAtMyType.size())
                .forEach(idx -> mostBookmarkedListAtMyType.get(idx).setNumOfBookmark((int)bookmarkRepository.findCountByReviewIdAndStatus(mostBookmarkedListAtMyType.get(idx).getReviewId(), ACTIVE)));


        //3. 이후 그 id값들을 가지고 imageUrl을 만들어서 setting
        /** 일단 첫 이미지 url들을 리스트로 정리한 후*/
        List<String> firstImageUrlList = mostBookmarkedListAtMyType.stream()
                .map(dto -> reviewImageRepository.findFirstImageByReviewId(dto.getReviewId()))
                .map(optionalReviewImage -> getFirstImageUrl(optionalReviewImage))
                .collect(Collectors.toList());

        /** 각 서머리 dto에 , 그 이미지 url들을 setting 해줌 */
        IntStream.range(0, mostBookmarkedListAtMyType.size())
                .forEach(idx -> mostBookmarkedListAtMyType.get(idx).setImageUrl(firstImageUrlList.get(idx)));

        //4. 리턴
        return mostBookmarkedListAtMyType;
    }

    /** ---------------------------------------------------------------------------------------------------------------------- */

    /**
     * [메인화면에서 인기있는 스타일을 보여주는 서비스]
     * */
    public List<MainSummaryDto> getMainReviewList(Pageable pageable){

        //1. reviewId를 기준으로 북마크를 groupBy한 후 - 그 count 값에 따라 북마크를 내림창순 정렬해서 - review와 join 하여 가져옴 -> 그게 ReviewSummaryDto
        List<MainSummaryDto> mainReviewList = bookmarkRepository.findMainReviewList(pageable);

        //1_2. 만약 리스트가 비었다면 그대로 리턴
        if(CollectionUtils.isEmpty(mainReviewList)){
            return mainReviewList;
        }

        //2. 이후 그 id값들을 가지고 imageUrl을 만들어서 setting
        /** 일단 첫 이미지 url들을 리스트로 정리한 후*/
        List<String> firstImageUrlList = mainReviewList.stream()
                .map(dto -> reviewImageRepository.findFirstImageByReviewId(dto.getReviewId()))
                .map(optionalReviewImage -> getFirstImageUrl(optionalReviewImage))
                .collect(Collectors.toList());

        /** 각 서머리 dto에 , 그 이미지 url들을 setting 해줌 */
        IntStream.range(0, mainReviewList.size())
                .forEach(idx -> mainReviewList.get(idx).setImageUrl(firstImageUrlList.get(idx)));

        //3. 리턴
        return mainReviewList;
    }

    /** ---------------------------------------------------------------------------------------------------------------------- */

    /**
     * [메인화면에서 MyType에 맞는 리뷰를 보여주는 서비스]
     * */

    public List<MainSummaryDto> getMainMyTypeList (Long memberId, Pageable pageable){
        //0. Member 조회
        Member findMember = getMember(memberId, ACTIVE);

        //1. reviewId를 기준으로 북마크를 groupBy한 후 - 그 count 값에 따라 북마크를 내림창순 정렬해서 가져옴 -> 이때 MyType에 맞는 Review하고만 join 하여 가져옴
        List<MainSummaryDto> mainTypeList = bookmarkRepository.findMainTypeList(findMember.getGender(), findMember.getLengthStatus(), findMember.getCurlyStatus(), pageable);

        //1_2. 만약 리스트가 비었다면 그대로 리턴
        if(CollectionUtils.isEmpty(mainTypeList)){
            return mainTypeList;
        }

        //2. 이후 그 id값들을 가지고 imageUrl을 만들어서 setting
        /** 일단 첫 이미지 url들을 리스트로 정리한 후*/
        List<String> firstImageUrlList = mainTypeList.stream()
                .map(dto -> reviewImageRepository.findFirstImageByReviewId(dto.getReviewId()))
                .map(optionalReviewImage -> getFirstImageUrl(optionalReviewImage))
                .collect(Collectors.toList());

        /** 각 서머리 dto에 , 그 이미지 url들을 setting 해줌 */
        IntStream.range(0, mainTypeList.size())
                .forEach(idx -> mainTypeList.get(idx).setImageUrl(firstImageUrlList.get(idx)));

        //3. 리턴
        return mainTypeList;
    }

}

package review.hairshop.member.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import review.hairshop.common.enums.Status;
import review.hairshop.common.response.ApiException;
import review.hairshop.common.response.ApiResponseStatus;
import review.hairshop.common.utils.WebClientUtil;
import review.hairshop.member.Member;
import review.hairshop.member.dto.KakaoAuthDto;
import review.hairshop.member.dto.LoginParameterDto;
import review.hairshop.member.dto.MyPageParameterDto;
import review.hairshop.member.dto.response.MyPageResponseDto;
import review.hairshop.member.dto.response.NaverLocalResponseDto;
import review.hairshop.member.dto.response.WithdrawalResponseDto;
import review.hairshop.member.repository.MemberRepository;
import static review.hairshop.common.enums.Status.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    @Value("${kakao.url}")
    private String url;

    private final MemberRepository memberRepository;
    private final WebClientUtil webClientUtil;


    private Member getMember(Long memberId, Status status){
        Member findMember = memberRepository.findByIdAndStatus(memberId, status).orElseThrow(
                () -> {
                    throw new ApiException(ApiResponseStatus.INVALID_MEMBER, "로그인 된 회원이 아닙니다.");
                }
        );

        return findMember;
    }

    @Transactional
    public LoginParameterDto login(String accessToken){

        //1. 카카오 서버로 요청을 보내, accessToken에 대한 kakaoId 값을 얻어냄
        KakaoAuthDto kakaoAuthDto = webClientUtil.sendRequest(url, accessToken).blockFirst();

        //2_1. 해당 kakaoId값을 가지고 식별가능한 Member가 있다면 -> 그 Member를 조회하여 그 Member의 Id 값을 LoginParameterDto로 변화하여 리턴
        //2_2. 해당 kakaoId 값을 가지고 식별 가능한 Member가 없다면 -> 최초 로그인인 경우 이므로 , Member를 생성하여 저장한 후 , 그 Member의 Id값을 LoginParameterDto로 변환하여 리턴

        Member member = memberRepository.findByKakaoId(kakaoAuthDto.getId()).orElseGet(
                () -> {
                    return createNewMember(kakaoAuthDto.getId(), kakaoAuthDto.getProperties().get("nickname"));
                }
        );

        //3. 만약 조회한 회원이 한번 회원탈퇴하여 INACTIVE 한 회원일 경우 -> 재가입 요청이므로 , status 값을 ONGOING으로 업데이트 해준다.
        if(member.getStatus().equals(INACTIVE)){
            member.changeStatus(ONGOING);
        }


        return LoginParameterDto.builder()
                .memberId(member.getId())
                .status(member.getStatus())
                .build();
    }

    private Member createNewMember(Long kakaoId, String nickname){
        Member member = Member.builder()
                .kakaoId(kakaoId)
                .name(nickname)
                .status(Status.ONGOING)
                .build();

        memberRepository.save(member);

        return member;
    }

    /** -----------------------------------------------------------------------------------------------------------------------*/

    @Transactional
    public WithdrawalResponseDto withdrawal(Long memberId){

        //1. 로그인 된 멤버 조회 후 (여기서 페치조인을 쓰면 안됨!)
        Member findMember = getMember(memberId, ACTIVE);

        /** (리뷰 이미지는 리뷰에 종속적이기 때문에, 리뷰만 INACTIVE하게 만든다면 , 그 리뷰를 통해 이미지에 접근할 일은 없으니 , 결과적으로 이미지는 따로 건들지 않음)*/
        //2_1. 이 Member가 작성한 리뷰들을 모두 INACTIVE 하게 만듦 (batch size로 100개씩 다시 리뷰들을 가져온 후 -> 이들을 INACTIVE 하게 업데이트)
        findMember.getReviewList().forEach(r -> r.changeStatus(INACTIVE));

        //2_2. 이 Member가 북마크한 정보들을 모두 INACTIVE하게 만듦 (batch size로 100개씩 다시 북마크들을 가져온 후 -> 이들을 INACTIVE 하게 업데이트)
        findMember.getBookmarkList().forEach(b -> b.changeStatus(INACTIVE));

        //2_3. 마지막으로 Member의 status 값을 INACTIVE로 바꿈
        findMember.changeStatus(INACTIVE);

        //3. 이후 응답 리턴
        return WithdrawalResponseDto.builder()
                .nickname(findMember.getName())
                .build();
    }

    /** -----------------------------------------------------------------------------------------------------------------------*/

    @Transactional
    public MyPageResponseDto updateMyPageForLogin(Long memberId, MyPageParameterDto myPageParameterDto){

        //1. Member 조회 후
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> {
                    throw new ApiException(ApiResponseStatus.INVALID_MEMBER, "유효하지 않은 Member Id로 Member를 조회하려고 했습니다.");
                }
        );

        //2. 넘어온 정보로 3가지 필드 업데이트
        findMember.changeMemberInfo(myPageParameterDto.getGender(), myPageParameterDto.getLengthStatus(), myPageParameterDto.getCurlyStatus());

        //3. 이때 처음으로 회원 정보를 등록하는거면 -> findMember의 status가 ONGOING 이니까 ACTIVE로 변환
        if(findMember.getStatus().equals(Status.ONGOING)){
            findMember.changeStatus(ACTIVE);
        }

        //4. 이후 업데이트 된 정보를 그대로 넘김
        return  MyPageResponseDto.builder()
                .nickname(findMember.getName())
                .gender(findMember.getGender())
                .lengthStatus(findMember.getLengthStatus())
                .curlyStatus(findMember.getCurlyStatus())
                .build();
    }

    /** --------------------------------------------------------------------------------------------------------------*/

    @Transactional
    public MyPageResponseDto updateMyPage(Long memberId, MyPageParameterDto myPageParameterDto){

        //1. Member 조회 후
        Member findMember = getMember(memberId, ACTIVE);

        //2. 넘어온 정보로 2가지 필드 업데이트 (gender까지 들어오지만 -> gender는 업데이트 하지 않음!!!)
        findMember.changeMemberInfo(myPageParameterDto.getLengthStatus(), myPageParameterDto.getCurlyStatus());


        //3. 이후 업데이트 된 정보를 그대로 넘김
        return  MyPageResponseDto.builder()
                .nickname(findMember.getName())
                .gender(findMember.getGender())
                .lengthStatus(findMember.getLengthStatus())
                .curlyStatus(findMember.getCurlyStatus())
                .build();
    }

    /** --------------------------------------------------------------------------------------------------------------*/

    public MyPageResponseDto getMyPageInfo(Long memberId){

        //1. Member를 조회한 후
        Member findMember = getMember(memberId, ACTIVE);

        //2. MyPagaeInfo 만 추출하여 -> 그대로 DTO로 변환하여 반환
        return MyPageResponseDto.builder()
                                .nickname(findMember.getName())
                                .gender(findMember.getGender())
                                .lengthStatus(findMember.getLengthStatus())
                                .curlyStatus(findMember.getCurlyStatus())
                                .build();
    }

    /** --------------------------------------------------------------------------------------------------------------*/







}

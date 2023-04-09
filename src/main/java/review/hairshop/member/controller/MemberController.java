package review.hairshop.member.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import review.hairshop.common.response.ApiResponse;
import review.hairshop.member.dto.MyPageParameterDto;
import review.hairshop.member.dto.request.LoginRequestDto;
import review.hairshop.member.dto.LoginParameterDto;
import review.hairshop.member.dto.request.MyPageRequestDto;
import review.hairshop.member.dto.response.*;
import review.hairshop.member.service.MemberService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

import static review.hairshop.common.consts.SessionConst.MEMBER_ID;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    /**
     * [TEST API]
     * */


    /**
     * [API 1.] : 로그인
     * */
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@Validated @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request){

        LoginParameterDto loginParameterDto = memberService.login(loginRequestDto.getAccessToken());

        /** 아래의 작업을 통해 3가지 일이 수행됨
         *
         * 1) 랜덤한 session key 값을 생성해주고
         * 2) 그 session key와 대응되는 value로 memberId를 설정하여 , 그 한 쌍을 session store에 저장한 후
         * 3) 그 session key 값을 응답 cookie 값으로 저장
         * */

        /** 1. 세션이 있으면 신규 세션 반환 or 없으면 신규 세션 생성 (세션이란 결국 key와 value의 조합인데, 이때 빈 value에 key를 갖는 세션을 생셩) */
        HttpSession session = request.getSession();

        /** 2. 그 생성된 세션의 빈 value에 memberId 값을 넣어주고 , 그 value의 이름을 MEMBER_ID로 설정 - 이때 이름일 뿐 key가 아니라는 점 주의  */
        session.setAttribute(MEMBER_ID, loginParameterDto.getMemberId());

        return ApiResponse.success(
                LoginResponseDto.builder().status(loginParameterDto.getStatus()).build()
        );
    }

    /**
     * [API 2.] : 로그아웃
     * */

    @PostMapping("/logout")
    public ApiResponse<LogoutResponseDto> logout(HttpServletRequest request){

        /** 1. 기존 세션만을 조회하여 */
        HttpSession session = request.getSession(false);

        /** 2. session 값이 존재한다면 (null이 아니라면) -> 그 세션을 제거한다 */
        Optional.ofNullable(session).ifPresent(s -> s.invalidate());

        return ApiResponse.success(LogoutResponseDto.builder().resultMessage("로그아웃 되었습니다").build());
    }

    /**
     * [API 3.] : 회원 탈퇴
     * */
    @PatchMapping("/withdrawal")
    public ApiResponse<WithdrawalResponseDto> withdrawal(@RequestAttribute Long memberId, HttpServletRequest request){

        /** 1. INACTIVE 하게 만들고 */
        WithdrawalResponseDto withdrawalResponseDto = memberService.withdrawal(memberId);

        /** 2. 기존 세션만을 조회하여 */
        HttpSession session = request.getSession(false);

        /** 3. session 값이 존재한다면 (null이 아니라면) -> 그 세션을 제거한다 */
        Optional.ofNullable(session).ifPresent(s -> s.invalidate());

        return ApiResponse.success(withdrawalResponseDto);
    }

    /**
     * [API 4.] : 로그인 직후 회원 정보 설정
     * */
    @PatchMapping("/login/mypage")
    public ApiResponse<MyPageResponseDto> loginMyPage(@RequestAttribute Long memberId, @Validated @RequestBody MyPageRequestDto myPageRequestDto){

        MyPageParameterDto myPageParameterDto = MyPageParameterDto.builder()
                .gender(myPageRequestDto.getGender())
                .lengthStatus(myPageRequestDto.getLengthStatus())
                .curlyStatus(myPageRequestDto.getCurlyStatus())
                .build();

        return ApiResponse.success(memberService.updateMyPageForLogin(memberId, myPageParameterDto));
    }

    /**
     * [API 5.] : 회원정보 조회
     * */
    @GetMapping("/mypage")
    public ApiResponse<MyPageResponseDto> getMyPage(@RequestAttribute Long memberId){

        return ApiResponse.success(memberService.getMyPageInfo(memberId));
    }

    /**
     * [API 6.] : 회원 정보 수정
     * */
    @PatchMapping("/mypage")
    public ApiResponse<MyPageResponseDto> patchMyPage(@RequestAttribute Long memberId, @Validated @RequestBody MyPageRequestDto myPageRequestDto){

        MyPageParameterDto myPageParameterDto = MyPageParameterDto.builder()
                .lengthStatus(myPageRequestDto.getLengthStatus())
                .curlyStatus(myPageRequestDto.getCurlyStatus())
                .build();

        return ApiResponse.success(memberService.updateMyPage(memberId, myPageParameterDto));
    }






}

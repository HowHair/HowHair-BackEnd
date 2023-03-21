package review.hairshop.common.response;

import lombok.Getter;
/**
 * [ 1000단위 ] - 오류의 범위
 *  1000 : 요청 성공
 *  2 : Request 오류
 *  3 : Reponse 오류
 *  4 : DB, Server 오류
 *
 * [ 100단위 ] - 오류 도메인
 *  0 : 공통 오류
 *  1 : member 오류
 *  2 : review 오류
 *  3 : reviewImage 오류
 *  4 : bookmark 오류
 *  5 : hairType 오류
 *  6 : hairStyle 오류
 *  7 : hairTypeMapping 오류
 *  8 : hairStyleMapping 오류
 *
 *
 *
 * [10단위] - 오류 HTTP Method
 *  0~19 : Common
 *  20~39 : GET
 *  40~59 : POST
 *  60~79 : PATCH
 *  80~99 : else
 *
 *  [1 단위] - 그외 오류의 고유 식별자
 *          - 순서대로 설정해주면 됨
 */

/**  [ApiResponse 로 나갈 값들을 - 상황에 따른 열거형 값으로 미리 선언해 놓고 - 가져다 쓰는 형태를 위해서 사용]*/
@Getter
public enum ApiResponseStatus {

    /**
     * 1000 : 요청 성공
     * */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     * */
    MISSING_REQUEST_HEADER(false, 2000, "필수 헤더 값이 넘어오지 않았습니다."),
    MISSING_REQUEST_PARAMETER(false, 2001, "필수 쿼리파라미터 값이 넘어오지 않았습니다."),
    VALIDATION_FAIL(false, 2002, "요청한 값의 검증 로직에서 오류가 발견되었습니다."),
    INVALID_ENUM(false, 2003, "정의되지 않은 enum 값이 넘어왔습니다."),
    MAX_FILE_SIZE_EXCEEDED(false, 2004, "업로드 가능한 각 이미지 최대 사이즈는 15MB로 제한됩니다."),
    MAX_REQUEST_SIZE_EXCEEDED(false, 2005, "업로드 가능한 총 이미지 최대 사이즈는 100MB로 제한됩니다."),
    NO_LOGIN(false, 2006, "로그인된 사용자가 아닙니다."),
    INCORRECT_HTTP_METHOD(false,2005, "잘못 매칭된 HTTP 메소드로 요청이 들어왔습니다."),
    INVALUD_ACCESS_TOKEN(false,2101, "유효하지 않은 Access Token 입니다."),
    FAIL_SIGN_UP(false, 2141, "이미 존재하는 username 입니다."),
    FAIL_LOGIN(false, 2121, "아이디 또는 패스워드가 잘못되었습니다."),
    INVALID_MEMBER(false, 2122, "로그인된 회원이 아닙니다."),


    INVALID_REVIEW(false, 2221, "유효한 리뷰가 아닙니다"),
    INVALID_MEMBER_AT_REVIEW_DELETE(false, 2261, "해당 회원이 쓴 리뷰가 아니기 때문에, 해당 회원은 이 리뷰를 지울 수 없습니다."),

    FAIL_GET_ITEM_IMAGE(false, 2321, "복호화에 성공한 원본 경로에 저장된 이미지가 없습니다."),
    INVALID_EXTENSION(false, 2341, "지원하지 않는 이미지 파일 형식 입니다."),

    /**
     * 3000 : Response 오류
     * */

    /**
     * 4000 : Database, Server 오류
     * */
    INTERNAL_SERVER_ERROR(false, 4000, "예상하지 못한 예외가 발생하였습니다."),
    FAIL_ENCRYPT(false, 4001, "AES256 암호화 과정에서 예와가 발생하였습니다."),
    FAIL_DECRYPT(false, 4002, "AES256 복호화 과정에서 예외가 발생하였습니다."),
    FAIL_GET_REVIEW_IMAGE(false, 4003, "이미지를 가져오는데 실패하였습니다."),
    FAIL_SAVE_IMAGE(false, 4004, "이미지를 저장하는데 실패했습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;


    /**
     * ApiResponseStatus에서 각 해당하는 코드를 생성자로 맵핑
     * [열겨형의 생성자 - 반드시 private]
     * <이렇게 열거형 생성자를 정의하면 - 열거형 값의 선언시 , 소괄호를 통해 생성자에 인자를 전달할 수 있음 >
     *     : 그렇게 되면 결과적으로는 열거형 타입의 (열거형도 class) 객체가 생성되고 - 그 객체의 필드가 소괄호에 인자로 전달한 값 대로 초기화되는것!
     * */

    private ApiResponseStatus(boolean isSuccess, int code, String message){
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

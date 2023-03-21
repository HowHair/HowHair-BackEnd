package review.hairshop.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import review.hairshop.common.response.ApiException;
import review.hairshop.common.response.ApiResponse;
import review.hairshop.common.response.ApiResponseStatus;
import review.hairshop.common.response.validation.ValidationFail;
import review.hairshop.common.response.validation.ValidationFailForField;
import review.hairshop.common.response.validation.ValidationFailForObject;


import java.util.stream.Collectors;

import static review.hairshop.common.response.ApiResponseStatus.INVALID_ENUM;
import static review.hairshop.common.response.ApiResponseStatus.INVALUD_ACCESS_TOKEN;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {


    /** [우리가 정의한 예외 상황 발생시에 -> 따른 실패 응답]*/
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse exHandler(ApiException e){
        log.error("EXCEPTION = {}, INTERNAL_MESSAGE = {}", e.getStatus(), e.getInternalMessage());
        return ApiResponse.fail(e.getStatus());
    }

    /** [OAuth 수행시 , 유효하지 않은 토큰값이 넘어갔을 때 발생하는 예외] */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse authHandler(WebClientResponseException e){
        log.error("EXCEPTION = {}, INTERNAL_MESSAGE = {}", e, e.getMessage());
        return ApiResponse.fail(INVALUD_ACCESS_TOKEN);
    }

    /** [필수 헤더값이 없을 때 발생하는 예외 처리에 대한 -> 실패 응답] */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse missRequestHeaderExHandler(MissingRequestHeaderException e){
        log.error("EXCEPTION = {}, INTERNAL_MESSAGE = {}", e, e.getMessage());
        return ApiResponse.fail(ApiResponseStatus.MISSING_REQUEST_HEADER);
    }

    /** [필수 쿼리 파라미터가 없을 때 발생하는 예외 처리에 대한 -> 실패 응답] */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse missingRequestParamExHandler(MissingServletRequestParameterException e){
        log.error("EXCEPTION = {}, INTERNAL_MESSAGE = {}", e, e.getMessage());
        return ApiResponse.fail(ApiResponseStatus.MISSING_REQUEST_PARAMETER);
    }


    /** Bean Validation 기능에 따른 검증에서 오류가 존재하여,
     *  BindingResult에 FieldError 또는 ObjectError가 있음에도
     *  Controller에서 이에 대한 에러 핸들링을 하지 않는 경우  -> BindException이 터진다 -> 그러면 이 ExceptionHandler까지 예외가 올라오고 , 여기서 일괄적으로 처리한다 */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse bindExHandler(BindException e, BindingResult bindingResult){
        log.error("EXCEPTION = {}, INTERNAL_MESSAGE = {}", e, e.getMessage());
        ValidationFail validationFail = makeValidationError(bindingResult);
        return ApiResponse.failBeanValidation(validationFail);

    }

    private ValidationFail makeValidationError(BindingResult bindingResult){
        return ValidationFail.builder()
                .fieldList(bindingResult.getFieldErrors().stream()
                        .map(f -> new ValidationFailForField(f))
                        .collect(Collectors.toList()))
                .objectList(bindingResult.getGlobalErrors().stream()
                        .map(o -> new ValidationFailForObject(o))
                        .collect(Collectors.toList()))
                .build();
    }
    /** [정의되지 않은 enum 값이 넘어왔을 떄 터지는 Exception을 처리하는 Exception Handler] */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse inCorrectEnum(HttpMessageNotReadableException e){
        log.error("EXCEPTION = {} , EXCEPTION_MESSAGE = {}, INTERNAL_MESSAGE = {}", INVALID_ENUM, e.getMessage(),"정의하지 않은, 잘못된 enum 값이 요청으로 들어왔습니다.");
        return ApiResponse.fail(INVALID_ENUM);
    }

    /**
     * [설정한 HTTP 메소드와 다른 HTTP 메소드로 요청이 들어온 경우]
     * */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse inCorrectHttpMethod(HttpRequestMethodNotSupportedException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return ApiResponse.fail(ApiResponseStatus.INCORRECT_HTTP_METHOD);
    }


    /** -------------------------------------------------------------------------------*/

    /** [15MB 이상의 이미지 파일을 업로드 시도했을 때 발생하는 예외에 대한 처리] */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse makeFileSizeExHandler(FileSizeLimitExceededException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return ApiResponse.fail(ApiResponseStatus.MAX_FILE_SIZE_EXCEEDED);
    }

    /** [총 업로드한 이미지 파일의 총량이 , 정해진 수치를 넘었을 때 발생하는 예외에 대한 처리] */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse maxRequestSizeExHandler(SizeLimitExceededException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return ApiResponse.fail(ApiResponseStatus.MAX_REQUEST_SIZE_EXCEEDED);
    }


    /**--------------------------------------------------------------------------------*/
    /** [그 외 우리가 예상하지 못한 예외가 발생했을 때 잡아주는 처리] */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse internalServerExHandler(Exception e){
        log.error("EXCEPTION = {}, INTERNAL_MESSAGE = {}", e, e.getMessage());
        return ApiResponse.fail(ApiResponseStatus.INTERNAL_SERVER_ERROR);
    }

}

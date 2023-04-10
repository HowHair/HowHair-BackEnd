package review.hairshop.review_facade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewIdRequestDto {

    @NotNull(message = "리뷰 아이디 값은 필수 입니다.")
    private Long reviewId;
}

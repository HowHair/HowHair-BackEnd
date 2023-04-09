package review.hairshop.bookmark.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequestDto {
    @NotNull(message = "리뷰 아이디는 필수 값 입니다.")
    private Long reviewId;
}

package review.hairshop.review_facade.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteReviewResponseDto {
    private Long reviewId;
}

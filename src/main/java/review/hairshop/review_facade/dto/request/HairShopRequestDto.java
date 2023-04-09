package review.hairshop.review_facade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class HairShopRequestDto {
    @NotNull(message = "미용실 이름은 필수 입니다.")
    private String hairshopName;
}

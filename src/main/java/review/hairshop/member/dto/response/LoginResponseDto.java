package review.hairshop.member.dto.response;

import lombok.*;
import review.hairshop.common.enums.Status;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private Status status;


}

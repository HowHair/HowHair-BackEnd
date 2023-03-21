package review.hairshop.member.dto;


import lombok.*;
import review.hairshop.common.enums.Status;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginParameterDto {

    private Long memberId;
    private Status status;
}

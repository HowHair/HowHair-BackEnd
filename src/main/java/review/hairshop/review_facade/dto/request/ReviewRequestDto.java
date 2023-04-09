package review.hairshop.review_facade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.surgery.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

    @NotNull(message = "미용실 이름은 필수값 입니다.")
    private String hairShopName;

    private String hairShopNumber;

    private String hairShopAddress;

    @Min(value = 1 , message = "만족도 최소값은 1 입니다.")
    @Max(value = 5, message = "만족도 최댓값은 5 입니다.")
    private int satisfaction;

    @NotNull(message = "시술 날짜는 필수 입력 입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate surgeryDate;

    private String designerName;

    @NotNull(message = "커트 종류 선택은 필수 입니다.")
    private HairCut hairCut;
    @NotNull(message = "염색 종류 선택은 필수 입니다.")
    private Dyeing dyeing;
    @NotNull(message = "펌 종류 선택은 필수 입니다.")
    private Perm perm;
    @NotNull(message = "매직 종류 선택은 필수 입니다.")
    private Straightening straightening;

    @NotNull(message = "머리 길이는 필수 입니다.")
    private LengthStatus lengthStatus;

    @Min(value = 0, message = "최소 가격은 0원 입니다.")
    private int price;

    private String content;

    @Size(max = 5, message = "사진은 최대 5장 까지 등록 가능합니다.")
    private List<MultipartFile> imageList = new ArrayList<>();
}

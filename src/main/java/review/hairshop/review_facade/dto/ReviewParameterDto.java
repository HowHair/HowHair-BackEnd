package review.hairshop.review_facade.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import review.hairshop.common.enums.LengthStatus;
import review.hairshop.common.enums.surgery.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewParameterDto {
    private String hairShopName;

    private String hairShopNumber;

    private String hairShopAddress;

    private int satisfaction;

    private LocalDate surgeryDate;

    private String designerName;

    private HairCut hairCut;
    private Dyeing dyeing;
    private Perm perm;
    private Straightening straightening;

    private LengthStatus lengthStatus;

    private int price;

    private String content;

    private List<MultipartFile> imageList = new ArrayList<>();
}

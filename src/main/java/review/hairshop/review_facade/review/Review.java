package review.hairshop.review_facade.review;

import lombok.*;
import review.hairshop.bookmark.Bookmark;
import review.hairshop.common.BasicEntity;
import review.hairshop.common.enums.*;
import review.hairshop.common.enums.surgery.*;
import review.hairshop.member.Member;
import review.hairshop.review_facade.review_image.ReviewImage;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BasicEntity{

    @Id @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    private int satisfaction;

    private String hairShopName;
    private String hairShopNumber;
    private String hairShopAddress;
    private String designerName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private LengthStatus lengthStatus;

    @Enumerated(EnumType.STRING)
    private CurlyStatus curlyStatus;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private HairCut hairCut;

    @Enumerated(EnumType.STRING)
    private Dyeing dyeing;

    @Enumerated(EnumType.STRING)
    private Perm perm;

    @Enumerated(EnumType.STRING)
    private Straightening straightening;

    private int price;

    private String content;

    private LocalDate surgeryDate;


    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "review")
    private List<Bookmark> bookmarkList = new LinkedList<>();

    @OneToMany(mappedBy = "review")
    private List<ReviewImage> reviewImageList = new LinkedList<>();


    /**
     * [변경 메서드]
     * */
    public void changeStatus(Status status){
        this.status = status;
    }

}

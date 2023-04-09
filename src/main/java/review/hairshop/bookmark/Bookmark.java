package review.hairshop.bookmark;

import lombok.*;
import review.hairshop.common.BasicEntity;
import review.hairshop.common.enums.Status;
import review.hairshop.member.Member;
import review.hairshop.review_facade.review.Review;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "bookmark")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bookmark extends BasicEntity{

    @Id @GeneratedValue
    @Column(name = "bookmark_id")
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * [변경 메서드]
     * */
    public void changeStatus(Status status){
        this.status = status;
    }

}

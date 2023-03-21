package review.hairshop.review_facade.review_image;


import lombok.*;
import review.hairshop.common.BasicEntity;
import review.hairshop.common.enums.Status;
import review.hairshop.review_facade.review.Review;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "review_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewImage extends BasicEntity{

    @Id @GeneratedValue
    @Column(name = "review_image_id")
    private Long id;

    private String path;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JoinColumn(name = "reveiw_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;
}

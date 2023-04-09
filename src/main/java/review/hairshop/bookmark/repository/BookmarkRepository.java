package review.hairshop.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import review.hairshop.bookmark.Bookmark;
import review.hairshop.common.enums.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> , BookmarkRepositoryCustom{

    Optional<Bookmark> findByMemberIdAndReviewId(Long memberId, Long reviewId);
    // 리턴타입이 리스트면 -> 없으면 빈 컬렉션 반환
    List<Bookmark> findByReviewIdAndStatus(Long reviewId, Status status);

    boolean existsByMemberIdAndReviewIdAndStatus(Long memberId, Long reviewId, Status status);

    @Query("select count(b) from Bookmark b where b.review.id=:reviewId and b.status=:status")
    long findCountByReviewIdAndStatus(@Param("reviewId") Long reviewId, @Param("status") Status status);

}

package review.hairshop.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import review.hairshop.common.enums.Status;
import review.hairshop.member.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByIdAndStatus(Long id, Status status);

    Optional<Member> findByKakaoId(Long kakaoId);

    boolean existsByIdAndStatus(Long id, Status status);
}

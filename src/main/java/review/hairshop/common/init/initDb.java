//package review.hairshop.common.init;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import review.hairshop.member.Member;
//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class initDb {
//
//    private final InitService initService;
//
//
//    /** 샘플데이터로 DB 초기화 -> 스프링 빈 의존관계 주입이 끝난 직후 수행되는 로직 by @PostConstruct*/
//    @PostConstruct
//    void init(){
//        initService.doInit1();
//    }
//
//    /** 실질적으로 샘플 데이터를 DB에 넣는 Service 로직 */
//    @Service
//    @Transactional
//    @RequiredArgsConstructor
//    static class InitService{
//
//        private final EntityManager em;
//
//
//        public void doInit1() {
//            Member member1 = Member.builder().name("김용준").build();
//            Member member2 = Member.builder().name("추지은").build();
//            em.persist(member1); em.persist(member2);
//
//
//        }
//
//    }
//
//
//}

package study.querydsl;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional // Transactional이 코드가 끝난뒤에 롤백시킨다.
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    // 필드로 뺄 수 있다 동시성 문제 없도록 설계되어있다.
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    @BeforeEach
    public void before() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10 , teamA);
        Member member2 = new Member("member2", 20 , teamA);
        Member member3 = new Member("member3", 30 , teamB);
        Member member4 = new Member("member4", 40 , teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        // member1을 찾아라.
        String qlString = "select m from Member m " +
                "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        // 방법1
        // QMember m = new QMember("m");      같은 테이블을 조인해야하는 경우에만 사용. 그렇지 않은경우에는 권장 방법3으로 사용해라

        // 방법2
        // QMember m = QMember.member;

//         Member findMember = queryFactory
//                .select(m)
//                .from(m)
//                .where(m.username.eq("member1"))
//                .fetchOne();

        // 방법 3 (권장)  static import
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }


}

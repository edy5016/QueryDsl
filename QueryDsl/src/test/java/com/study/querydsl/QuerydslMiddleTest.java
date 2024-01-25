package com.study.querydsl;
import static com.study.querydsl.domain.QMember.member;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.domain.Member;
import com.study.querydsl.domain.Team;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class QuerydslMiddleTest {
	@PersistenceContext
	EntityManager em;
	
	JPAQueryFactory queryFactory;
	
	@BeforeEach
	public void before() {
		queryFactory = new JPAQueryFactory(em);
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
	}
	
	@Test
	public void simpleProjection() {
		List<String> result = queryFactory
			.select(member.username)
			.from(member)
			.fetch();
		
		for (String s : result) {
			System.out.println("s = " + s);
		}
	}
	@Test
	public void tupleProjection() {
		List<Tuple> result = queryFactory
			.select(member.username, member.age)
			.from(member)
			.fetch();
		
		for (Tuple s : result) {
			String username = s.get(member.username);
			Integer age = s.get(member.age);
			System.out.println("username =" +username);
			System.out.println("age = " + age);
			
		}
	}
	
}

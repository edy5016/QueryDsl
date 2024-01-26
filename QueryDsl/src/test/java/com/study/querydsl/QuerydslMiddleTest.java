package com.study.querydsl;
import static com.study.querydsl.domain.QMember.member;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.domain.Member;
import com.study.querydsl.domain.QMember;
import com.study.querydsl.domain.Team;
import com.study.querydsl.dto.MemberDto;
import com.study.querydsl.dto.QMemberDto;
import com.study.querydsl.dto.UserDto;

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
	
	@Test
	public void findDtoByJPQL() { 
		List<MemberDto> result= em.createQuery("select new com.study.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
						.getResultList();

		for(MemberDto memberDto : result) {
			System.out.println("memberDto =" + memberDto);
		}
	}
	
	@Test
	public void findDtoBySetter() {
		List<MemberDto> result = queryFactory
			.select(Projections.bean(MemberDto.class,
					member.username, 
					member.age))
			.from(member)
			.fetch();
		
		for (MemberDto m : result) {
			System.out.println("m = "+ m);
		}
	}
	
	@Test
	public void findDtoByField() {
		List<MemberDto> result = queryFactory
			.select(Projections.fields(MemberDto.class,
					member.username, 
					member.age))
			.from(member)
			.fetch();
		
		for (MemberDto m : result) {
			System.out.println("m  fields= "+ m);
		}
	}
	@Test
	public void findDtoByConstructor() {
		List<MemberDto> result = queryFactory
			.select(Projections.constructor(MemberDto.class,
					member.username, 
					member.age))
			.from(member)
			.fetch();
		
		for (MemberDto m : result) {
			System.out.println("m  constructor= "+ m);
		}
	}
	@Test
	public void findDtoByAlias() {
		QMember memberSub = new QMember("memberSub");
		List<UserDto> result = queryFactory
			.select(Projections.fields(UserDto.class,
					member.username.as("name"), 
					ExpressionUtils.as(JPAExpressions
							.select(memberSub.age.max())
							.from(memberSub), "age")
					))
			.from(member)
			.fetch();
		
		for (UserDto m : result) {
			System.out.println("Alias= "+ m);
		}
	}
	
	@Test
	public void findDtoByQueryProjection() {
		List<MemberDto> result= queryFactory
			.select(new QMemberDto(member.username, member.age))
			.from(member)
			.fetch();
		
		for (MemberDto m : result) {
			System.out.println("MemberDto= "+ m);
		}
		
	}
	
	@Test
	public void dynamicQuery_BooleanBuilder() throws Exception {
		 String usernameParam = "member1";
		 Integer ageParam = 10;
		 
		 List<Member> result = searchMember1(usernameParam, ageParam);
		 Assertions.assertThat(result.size()).isEqualTo(1);
	}
	
	private List<Member> searchMember1(String usernameCond, Integer ageCond) {
		 BooleanBuilder builder = new BooleanBuilder();
		 if (usernameCond != null) {
				 builder.and(member.username.eq(usernameCond));
		 }
		 if (ageCond != null) {
				 builder.and(member.age.eq(ageCond));
		 }
		 return queryFactory
						 .selectFrom(member)
						 .where(builder)
						 .fetch();
	}
	
	@Test
	public void dynamicQuery_WhereParam() throws Exception {
			 String usernameParam = "member1";
			 Integer ageParam = 10;

			 List<Member> result = searchMember2(usernameParam, ageParam);
			 Assertions.assertThat(result.size()).isEqualTo(1);
		}

	private List<Member> searchMember2(String usernameCond, Integer ageCond) {
		 return queryFactory
							 .selectFrom(member)
							 .where(usernameEq(usernameCond), ageEq(ageCond))
							 .fetch();
	}
	private BooleanExpression usernameEq(String usernameCond) {
		return usernameCond != null ? member.username.eq(usernameCond) : null;
	}

	private BooleanExpression ageEq(Integer ageCond) {
		return ageCond != null ? member.age.eq(ageCond) : null;
	}

	
	// member1 = 10 => 비회원
	// member2 = 20 => 비회원
	// member3 = 30 => 회원
	// member4 = 40 => 회원
 	/**
 	 *  벌그연산은 항상 조심. 영속성 컨텍스트에 올라가있어서 수정된 사항 영속컨텍스 상태가 다름
 	 *  항상 벌크연산후 영속성 초기화 em.flush(); em.clear();
 	 */
	@Test
 	@Commit
	public void bulkUpdate() { 
		queryFactory
			.update(member)
			.set(member.username, "비회원")
			.where(member.age.lt(28))
			.execute();
	}
	
	
	@Test
 	@Commit
	public void bulkAdd() { 
		long count = queryFactory
				 .update(member)
				 .set(member.age, member.age.add(1))
				 .execute();
	}
	@Test
 	@Commit
	public void bulkDel() { 
		long count = queryFactory
				 .delete(member)
				 .where(member.age.gt(18))
				 .execute();
	}
}

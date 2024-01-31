package com.study.querydsl.repository;

import java.util.List;
import java.util.Optional;
import static com.study.querydsl.domain.QMember.member;
import static com.study.querydsl.domain.QTeam.team;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.domain.Member;
import com.study.querydsl.dto.MemberSearchCondition;
import com.study.querydsl.dto.MemberTeamDto;
import com.study.querydsl.dto.QMemberTeamDto;

import jakarta.persistence.EntityManager;

@Repository
public class MemberJpaRepository {
	private final EntityManager em;
	private final JPAQueryFactory queryFactory;
	
	public MemberJpaRepository(EntityManager em, JPAQueryFactory queryFactory) {
		this.em = em;
//		this.queryFactory = new JPAQueryFactory(em);
		this.queryFactory = queryFactory; // 빈등록후 이렇게사용도 가능
	}
	public void save(Member member) {
		em.persist(member);
	}
	public Optional<Member> findById(Long id) {
		Member findMember = em.find(Member.class, id);
		return Optional.ofNullable(findMember);
	}

	public List<Member> findAll() {
		return em.createQuery("select m from Member m", Member.class).getResultList();
	}
	
 	public List<Member> findAll_Querydsl() {
		return queryFactory
					.selectFrom(member)
					.fetch();
	}
	
	public List<Member> findByUsername_Querydsl(String username) {
		return queryFactory
					.selectFrom(member)
					.where(member.username.eq(username))
					.fetch();
	}
	public List<Member> findByUsername(String username) {
		 return em.createQuery("select m from Member m where m.username = :username", Member.class)
				 .setParameter("username", username)
				 .getResultList();
	}
	
	// Builder 사용
		// 회원명, 팀명, 나이(ageGoe, ageLoe)
	public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {
		BooleanBuilder builder = new BooleanBuilder();
		
		if(StringUtils.hasText(condition.getUsername())) {
			builder.and(member.username.eq(condition.getUsername()));
		}
		if(StringUtils.hasText(condition.getTeamName())) {
			builder.and(team.name.eq(condition.getTeamName()));
		}
		if (condition.getAgeGoe() != null) {
			builder.and(member.age.goe(condition.getAgeGoe()));
		}
		if (condition.getAgeLoe() != null) {
			builder.and(member.age.loe(condition.getAgeLoe()));
		}
		return queryFactory
					.select(new QMemberTeamDto(
							member.id.as("memberId"),
							member.username,
							member.age,
							team.id.as("teamId"),
							team.name.as("teamName")))
					.from(member)
					.leftJoin(member.team, team)
					.where(builder)
					.fetch();
	}
	
	public List<MemberTeamDto> search(MemberSearchCondition condition) {
		return queryFactory
				.select(new QMemberTeamDto(
						member.id.as("memberId"),
						member.username,
						member.age,
						team.id.as("teamId"),
						team.name.as("teamName")))
				.from(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()),
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), 
						ageLoe(condition.getAgeLoe()))
				.fetch();
	}
	
	private BooleanExpression teamNameEq(String teamName) {
		return StringUtils.hasText(teamName) ?  team.name.eq(teamName) : null;
	}
	private BooleanExpression usernameEq(String username) {
		// TODO Auto-generated method stub
		return StringUtils.hasText(username) ?  member.username.eq(username) : null ;
	}
	private BooleanExpression ageGoe(Integer ageGoe) {
		return ageGoe == null ? null : member.age.goe(ageGoe);
	}
	private BooleanExpression ageLoe(Integer ageLoe) {
		return ageLoe == null ? null : member.age.loe(ageLoe);
	}
	
	// where 파라미터 방식은 이런식으로 재사용이 가능하다.
	public List<Member> findMember(MemberSearchCondition condition) {
		return queryFactory
				.selectFrom(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()),
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), 
						ageLoe(condition.getAgeLoe()))
				.fetch();
	}
}

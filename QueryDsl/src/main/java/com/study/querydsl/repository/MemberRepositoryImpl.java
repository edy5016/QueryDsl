package com.study.querydsl.repository;

import static com.study.querydsl.domain.QMember.member;
import static com.study.querydsl.domain.QTeam.team;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.domain.Member;
import com.study.querydsl.dto.MemberSearchCondition;
import com.study.querydsl.dto.MemberTeamDto;
import com.study.querydsl.dto.QMemberTeamDto;

import jakarta.persistence.EntityManager;

public class MemberRepositoryImpl extends QuerydslRepositorySupport implements MemberRepositoryCustom {

	private final JPAQueryFactory queryFactory;
//	
//	public MemberRepositoryImpl(EntityManager em) {
//		this.queryFactory = new JPAQueryFactory(em);
//	}
// ==> QuerydslRepositorySupport 추가시 많은 것들을 제공해줌
	public MemberRepositoryImpl(EntityManager em) {
		super(Member.class);
		this.queryFactory =  new JPAQueryFactory(em);
	}

	@Override
	public List<MemberTeamDto> search(MemberSearchCondition condition) {
		return from(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()), 
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), 
						ageLoe(condition.getAgeLoe()))
				.select(new QMemberTeamDto(
						member.id,
						member.username, 
						member.age, 
						team.id, team.name))
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

	@Override
	public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
//		QueryResults<MemberTeamDto> result = queryFactory
//				.select(new QMemberTeamDto(
//						member.id,
//						member.username, 
//						member.age, 
//						team.id, team.name))
//				.from(member)
//				.leftJoin(member.team, team)
//				.where(
//						usernameEq(condition.getUsername()), 
//						teamNameEq(condition.getTeamName()),
//						ageGoe(condition.getAgeGoe()), 
//						ageLoe(condition.getAgeLoe()))
//				.offset(pageable.getOffset())
//				.limit(pageable.getPageSize())
//				.fetchResults();
//		
//		List<MemberTeamDto> contents = result.getResults();
//		Long total = result.getTotal();
//		
//		return new PageImpl<>(contents, pageable, total);
		
		JPQLQuery<MemberTeamDto> jpaQuery = 
				 from(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()), 
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), 
						ageLoe(condition.getAgeLoe()))
				.select(new QMemberTeamDto(
						member.id,
						member.username, 
						member.age, 
						team.id, team.name));
		
		JPQLQuery<MemberTeamDto> query = getQuerydsl().applyPagination(pageable, jpaQuery);
		
		QueryResults<MemberTeamDto> contents = query.fetchResults();
		Long total = contents.getTotal();
		
		
		return new PageImpl<>(contents.getResults(), pageable, total);
		
	}

	@Override
	public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
		List<MemberTeamDto> result =  queryFactory
				.select(new QMemberTeamDto(
						member.id,
						member.username, 
						member.age, 
						team.id, team.name))
				.from(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()), 
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), 
						ageLoe(condition.getAgeLoe()))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();
		
		JPAQuery<MemberTeamDto> countQuery = queryFactory
				.select(new QMemberTeamDto(
						member.id,
						member.username, 
						member.age, 
						team.id, team.name))
				.from(member)
				.leftJoin(member.team, team)
				.where(
						usernameEq(condition.getUsername()), 
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), 
						ageLoe(condition.getAgeLoe()));
		
		return PageableExecutionUtils.getPage(result, pageable, () -> countQuery.fetchCount());
		//return new PageImpl<>(result, pageable, total);
	}
	
}

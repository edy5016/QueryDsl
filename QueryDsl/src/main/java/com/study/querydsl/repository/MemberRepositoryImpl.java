package com.study.querydsl.repository;

import java.util.List;

import org.hibernate.sql.results.spi.ResultsConsumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import static com.study.querydsl.domain.QMember.member;
import static com.study.querydsl.domain.QTeam.team;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.dto.MemberDto;
import com.study.querydsl.dto.MemberSearchCondition;
import com.study.querydsl.dto.MemberTeamDto;
import com.study.querydsl.dto.QMemberTeamDto;

import jakarta.persistence.EntityManager;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	public MemberRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}
	
	@Override
	public List<MemberTeamDto> search(MemberSearchCondition condition) {
		return queryFactory
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
		QueryResults<MemberTeamDto> result = queryFactory
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
				.fetchResults();
		
		List<MemberTeamDto> contents = result.getResults();
		Long total = result.getTotal();
		
		return new PageImpl<>(contents, pageable, total);
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
		
		Long total = queryFactory
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
				.fetchCount();
		
		return new PageImpl<>(result, pageable, total);
	}
	
}

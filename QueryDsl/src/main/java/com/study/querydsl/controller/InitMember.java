package com.study.querydsl.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.study.querydsl.domain.Member;
import com.study.querydsl.domain.Team;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Profile("local") //spring.profiles.active = local 인경우 실행
@Component
@RequiredArgsConstructor
public class InitMember {
	
	private final InitMemberService initMemberService;

	/*** @PostConstruct
	* @Transactional 스프링 라이프 사이클 떄문에 이부분을 구분 해줘야 됨 
	***/
	@PostConstruct
	public void init() {
		initMemberService.init();
	}
	
	@Component
	static class InitMemberService {
		
		@PersistenceContext
		private EntityManager em;
		
		@Transactional
		public void init() {
			Team teamA = new Team("teamA");
			Team teamB = new Team("teamB");
			em.persist(teamA);
			em.persist(teamB);
			
			for (int i = 0; i < 100; i++) {
				Team selectTeam = i % 2 == 0 ? teamA : teamB;
				em.persist(new Member("member"+i, i,selectTeam));
			}
		}
	}
}

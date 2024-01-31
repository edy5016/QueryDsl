package com.study.querydsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.domain.Member;
import com.study.querydsl.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class MemberJpaDataRepositoryTest {
	@PersistenceContext
	EntityManager em;
	JPAQueryFactory queryFactory;

	@PersistenceUnit
	EntityManagerFactory emf;
	
	@Autowired
	MemberRepository memberRepository;
	
	@Test
	public void basicTest() {
		Member member = new Member("member1", 10);
		memberRepository.save(member);
		
		Member findMember = memberRepository.findById(member.getId()).get();
		assertThat(findMember).isEqualTo(member);
		
		List<Member> result1 = memberRepository.findAll();
		assertThat(result1).containsExactly(member);
		
		List<Member> result2 = memberRepository.findByUsername("member1");
		assertThat(result2).containsExactly(member);
	}
}

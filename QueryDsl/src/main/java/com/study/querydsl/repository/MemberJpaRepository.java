package com.study.querydsl.repository;

import java.util.List;
import java.util.Optional;
import static com.study.querydsl.domain.QMember.member;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.domain.Member;

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
}

package com.study.querydsl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.querydsl.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	//select m from Member m where m.username = ?
	List<Member> findByUsername(String username); // method 이름으로 자동으로 쿼리를 만듬
}

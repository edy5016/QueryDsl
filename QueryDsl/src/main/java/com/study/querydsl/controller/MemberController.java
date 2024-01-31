package com.study.querydsl.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.querydsl.dto.MemberSearchCondition;
import com.study.querydsl.dto.MemberTeamDto;
import com.study.querydsl.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberJpaRepository memberJpaRepository;
	
	@GetMapping("/v1/members")
	public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
		return memberJpaRepository.search(condition);
	}
}

package com.study.querydsl.repository;

import java.util.List;

import com.study.querydsl.dto.MemberSearchCondition;
import com.study.querydsl.dto.MemberTeamDto;

public interface MemberRepositoryCustom {
	List<MemberTeamDto> search(MemberSearchCondition condition);
}

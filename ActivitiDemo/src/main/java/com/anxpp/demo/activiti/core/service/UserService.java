package com.anxpp.demo.activiti.core.service;

import java.util.List;

import com.anxpp.demo.activiti.core.entity.User;

public interface UserService {
	/**
	 * 通过部门获取领导ID
	 * @param dept
	 * @return
	 */
	List<String> getSimpleCheckerByDept(Long dept);
	Long countUser();
	void save(User user);
}

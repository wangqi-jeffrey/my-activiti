package com.anxpp.demo.activiti.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.anxpp.demo.activiti.core.entity.base.BaseEntity;

/**
 * 员工实体
 * @author anxpp.com
 */
@Entity
public class User extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 5922975917317707955L;
	//用户ID
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	//员工编号
	private String code;
	//姓名
	private String name;
	//职位
	private Integer position;
	//所属部门编码
	private Long dept;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public Long getDept() {
		return dept;
	}
	public void setDept(Long dept) {
		this.dept = dept;
	}
}

package com.anxpp.demo.activiti.simple.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 * 申请单实体
 * @author anxpp.com
 * 2016年12月24日 下午12:18:34
 */

import com.anxpp.demo.activiti.core.entity.base.BaseEntity;
@Entity
public class ApplySimple extends BaseEntity implements Serializable{
	private static final long serialVersionUID = 7116347844167214559L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String comtent;
	private Integer state;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getComtent() {
		return comtent;
	}
	public void setComtent(String comtent) {
		this.comtent = comtent;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
}

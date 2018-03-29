package com.anxpp.demo.activiti.simple.Config;

/**
 * 常量
 * @author anxpp.com
 * 2016年12月24日 下午12:54:47
 */
public class Constant {
	/**职位*/
	public final static Integer POSITION_GENERAL	= 0;	//普通员工
	public final static Integer POSITION_LEADER 	= 1;	//领导
	/**审核意见*/
	public final static Integer CHECK_PASS		= 1;	//审核通过
	public final static Integer CHECK_NOPASS	= 0; 	//审核不通过
	/**业务状态*/
	public final static Integer STATE_TO_CHECK	= 0; 	//待审核
	public final static Integer STATE_PASS		= 100; 	//审核通过
	public final static Integer STATE_NOPASS	= -1; 	//审核不通过
}

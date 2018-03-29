package cn.itcast.ssh.utils;

import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.itcast.ssh.dao.IEmployeeDao;
import cn.itcast.ssh.domain.Employee;
import cn.itcast.ssh.service.IEmployeeService;

public class SessionContext {

	public static final String GLOBLE_USER_SESSION = "globle_user";
	public static final String NEXT_ASSIGNEE = "next_assignee";
	
	public static void setUser(Employee user){
		if(user!=null){
			ServletActionContext.getRequest().getSession().setAttribute(GLOBLE_USER_SESSION, user);
		}else{
			ServletActionContext.getRequest().getSession().removeAttribute(GLOBLE_USER_SESSION);
		}
	}
	
	public static void setNextAssignee(String nextAssignee){
		WebApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext.getServletContext());
		IEmployeeDao employeeDao = (IEmployeeDao) ac.getBean("employeeDao");
		Employee emp = employeeDao.findEmployeeById(Long.parseLong(nextAssignee));
		ServletActionContext.getRequest().getSession().setAttribute(NEXT_ASSIGNEE, emp.getName());
	}
	
	public static String getNextAssignee(){
		return (String) ServletActionContext.getRequest().getSession().getAttribute(NEXT_ASSIGNEE);
	}
	
	public static Employee get(){
		return (Employee) ServletActionContext.getRequest().getSession().getAttribute(GLOBLE_USER_SESSION);
	}
}

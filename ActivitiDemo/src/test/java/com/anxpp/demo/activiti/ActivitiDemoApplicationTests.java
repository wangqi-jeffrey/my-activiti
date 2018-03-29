package com.anxpp.demo.activiti;
import java.util.Iterator;
import java.util.List;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.anxpp.demo.activiti.core.entity.User;
import com.anxpp.demo.activiti.core.service.UserService;
import com.anxpp.demo.activiti.simple.Config.Constant;
import com.anxpp.demo.activiti.simple.core.entity.ApplySimple;
import com.anxpp.demo.activiti.simple.core.service.ApplySimpleService;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiDemoApplicationTests {
	private final static Logger log = LoggerFactory.getLogger(ActivitiDemoApplicationTests.class);
	private static Long DEPT_TINY_SOFTWARE_STUDIO = 1L;
	private static Long DEPT_OTHER = 2L;
	@Autowired
	UserService userService;
	@Autowired
	ApplySimpleService simpleService;
	/**
	 * 测试程序启动
	 */
	@Test
	public void contextLoads() {
	}
	/**
	 * 测试数据库操作
	 */
	@Test
	public void testCURD(){
		Long countUser = userService.countUser();
		User user = new User();
		user.setName("anxpp0");
		user.setDept(DEPT_TINY_SOFTWARE_STUDIO);
		userService.save(user);
		Assert.assertEquals(new Long(countUser+1), userService.countUser());
	}
	/**
	 * 测试简单流程
	 */
	@Test
	public void testSimpleActiviti(){
		long startAt = System.currentTimeMillis();
		//添加申请用户
		User user = new User();
		user.setName("anxpp");
		user.setDept(DEPT_TINY_SOFTWARE_STUDIO);
		user.setPosition(Constant.POSITION_GENERAL);
		userService.save(user);
		//添加审核用户
		User userLeader1 = new User();
		userLeader1.setName("anxpp1");
		userLeader1.setDept(DEPT_TINY_SOFTWARE_STUDIO);
		userLeader1.setPosition(Constant.POSITION_LEADER);
		userService.save(userLeader1);
		User userLeader2 = new User();
		userLeader2.setName("anxpp2");
		userLeader2.setDept(DEPT_OTHER);
		userLeader2.setPosition(Constant.POSITION_LEADER);
		userService.save(userLeader2);
		Long countHis = simpleService.countProcess();
		Long countLeader1Task = simpleService.countTask(userLeader1.getId());
		Long countLeader2Task = simpleService.countTask(userLeader2.getId());
		//创建请假申请
		ApplySimple applySimple = new ApplySimple();
		applySimple.setInsertBy(user.getId());
		applySimple.setComtent("有事请假...");
		//启动请假流程
		simpleService.startProcess(applySimple);
		/**断言历史流程+1*/
		Assert.assertEquals(simpleService.countProcess(), new Long(countHis+1));
		/**断言领导任务变化*/
		Assert.assertEquals(simpleService.countTask(userLeader1.getId()), new Long(countLeader1Task+1));
		Assert.assertEquals(countLeader2Task, simpleService.countTask(userLeader2.getId()));
		//获取用户任务
		List<Task> taskUserLeader1 = simpleService.getTaskByUid(userLeader1.getId());
		//处理任务
		Iterator<Task> iterator = taskUserLeader1.iterator();
		while(iterator.hasNext()){
			Task task = iterator.next();
			/**断言任务节点名称*/
			Assert.assertEquals("领导审批", task.getName());
			simpleService.completeSimpleCheck(task.getId(), ApplySimpleService.STATE_PASS);
		}
		/**断言领导任务变化*/
		Assert.assertEquals(countLeader1Task, simpleService.countTask(userLeader1.getId()));
		Assert.assertEquals(countLeader2Task, simpleService.countTask(userLeader2.getId()));
		System.err.println("asdf");
		log.info("测试完成，花费时间："+(System.currentTimeMillis()-startAt));
	}
}
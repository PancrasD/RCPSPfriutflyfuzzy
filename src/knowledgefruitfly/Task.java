package knowledgefruitfly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task {
    //任务/操作ID
	private int taskID;
	//任务工期即执行时间  根据分配的资源调整
	private int duration;
	//紧前
	private List<Integer> presecessorIDs=new ArrayList<>();
	//紧前任务数
	private int pretasknum;
	//紧后任务IDS
	private List<Integer> successorTaskIDS = new  ArrayList<>();
	//任务可用的资源
	private List<Integer> capableMachineIDs=new ArrayList<>();
	private List<Integer> capaleresourceIDs = new ArrayList<>();
	//任务的开始时间
	private int startTime;
	//任务的结束时间
	private int finishTime;
	public Task() {
		
	}
}

package knowledgefruitfly;

import java.util.ArrayList;
import java.util.List;

public class Worker {
	private int workerID;
	//存储的是机器ID
	private List<Integer> capbleMachines=new ArrayList<>();
	/*
	 * 初始化 一个工人能操作的机器   对机器添加能操作的工人
	 */
    public Worker(int workerID) {
    	this.workerID=workerID;
    }
	public int getWorkerID() {
		return workerID;
	}
	public void setWorkerID(int workerID) {
		this.workerID = workerID;
	}
	public List<Integer> getCapbleMachines() {
		return capbleMachines;
	}
	public void setCapbleMachines(List<Integer> capbleMachines) {
		this.capbleMachines = capbleMachines;
	}
    
}

package knowledgefruitfly;

import java.util.ArrayList;
import java.util.List;

public class Resource {
	//资源ID
	private int resourceID;
	private int machineID;
	private int workerID;
	private boolean exit;
	public Resource() {
		
	}
	public Resource(int resID,int macID,int workerID) {
		this.resourceID=resID;
		this.machineID=macID;
		this.workerID=workerID;
		Machine mac=new Machine(machineID);
		if(mac.getCapbleworkers().contains(workerID)) {
			this.exit=true;
		}
		
	}

}

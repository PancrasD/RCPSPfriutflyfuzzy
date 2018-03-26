package knowledgefruitfly;

import java.util.ArrayList;
import java.util.List;

public class Machine {
	private int machineID;
	private List<Integer> capbleworkers=new ArrayList<>();
	public  Machine(int macID) {
		this.machineID=macID;
	}
	public int getMachineID() {
		return machineID;
	}
	public void setMachineID(int machineID) {
		this.machineID = machineID;
	}
	public List<Integer> getCapbleworkers() {
		return capbleworkers;
	}
	public void setCapbleworkers(List<Integer> capbleworkers) {
		this.capbleworkers = capbleworkers;
	}
	

}

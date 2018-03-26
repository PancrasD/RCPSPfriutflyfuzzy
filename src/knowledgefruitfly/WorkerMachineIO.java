package knowledgefruitfly;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkerMachineIO {
	/*
	 * @param workersnumber工人数
	 * @param machineworkers
	 */
	private List<Map<Integer,List<Integer>>> workerMachines=new ArrayList<>();
	private List<Integer> workersnumber=new ArrayList<>();
	public WorkerMachineIO(String file) {
		readWorkerMachines(file);
	}
	/*
	 * 读取文件
	 */
	private  void readWorkerMachines(String file){
		BufferedReader read=null;
		try {
			read=new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("file not exit");
		}
		try {
			String line=read.readLine();
			this.skipTo(read,line,"M&W");
			this.readInitial(read);
			
		}catch(IOException e) {
			
		}finally {
			try {
				read.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void readInitial(BufferedReader read) throws IOException {
		List<Map<Integer,List<Integer>>> workermachines=new ArrayList<>();
		/*
		 * Map key:WorkerID value:machineID list
		 */
		Map<Integer,List<Integer>> workerList=new TreeMap<>();
		List<Integer> nWorker=new ArrayList<>();
		List<Integer> machineIDList=new ArrayList<>();
		String line=read.readLine();//此行舍弃
		Pattern p=null;
		Matcher m=null;
		while(!(line=read.readLine()).equals("")&&!line.startsWith("DP1-6")) {
			int i=1;//工人ID
			String[] strs=line.split(" +");
			nWorker.add(Integer.parseInt(strs[1]));
			p=Pattern.compile("(M\\d)+");
		    m=p.matcher(line);
		    while(m.find()) {
		    	String str=m.group();
		    	String[]machines=str.split("M");
		    	for(int ip=1;ip<machines.length;ip++) {
		    		machineIDList.add(Integer.parseInt(machines[ip]));
		    	}
			workerList.put(i++, new ArrayList<>(machineIDList));
			machineIDList.clear();
		    }
		    workermachines.add(new TreeMap<>(workerList));
		}
		this.setWorkerMachines(workermachines);
		this.setWorkersnumber(nWorker);
		
	}
	private String skipTo(BufferedReader read,String line,String mark) throws IOException {
		while(null!=line&&!line.startsWith(mark)) {
			line=read.readLine();
		}
		return line;
	}
	public List<Map<Integer, List<Integer>>> getWorkerMachines() {
		return workerMachines;
	}
	public void setWorkerMachines(List<Map<Integer, List<Integer>>> workerMachines) {
		this.workerMachines = workerMachines;
	}
	public List<Integer> getWorkersnumber() {
		return workersnumber;
	}
	public void setWorkersnumber(List<Integer> workersnumber) {
		this.workersnumber = workersnumber;
	}

	  
}

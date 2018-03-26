package knowledgefruitfly;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
/*
 * @param ntasks 任务数/操作数
 */
public class CaseMK  {
	
	public CaseMK(RCPSPIOMK iomk,WorkerMachineIO iomw) {
		creatOneByone(iomk, iomw);
	}
	/*
	 * 利用读取的所有工作 机器 操作 工人机器关系 生成案例
	 * 创建MK所有的案例
	 * @param cases 案例数量
	 * @param njobs 每项案例的工作数
	 * @param nmac 每项案例的机器数
	 * @param sumop 每项案例的所有工作的操作总和
	 * @param nop 每项案例中每项工作可包含的操作数目范围
	 * @param proc 每项案例中每项操作的执行时间范围
	 * @param diata 当为每项操作分配具体的资源时，操作时间的波动值
	 * @param opList 操作数 list
	 * @param procTimeList 操作执行时间list
	 * @param diataList 每项操作波动值list
	 * @param opCapableMacList Map 操作ID-能执行机器IDs
	 * @param workerMachines Map key:workID value:能操作机器IDs list
	 */
	public void creatOneByone(RCPSPIOMK iomk,WorkerMachineIO iomw) {
		int cases=iomk.getNjob().size();
		int njobs;
		int nmac;
		int nworker;
		int sumop;//不要
		Integer[] nop;
		int meq;//关系由外部给定
		Integer[] proc;
		Integer[] diata;
		List<Integer> opList=new ArrayList<>();
		List<Integer> procTimeList=new ArrayList<>();
		List<Integer> diataList=new ArrayList<>();
		Map<Integer,List<Integer>> workerMachines=new TreeMap<>();
		Map<Integer,List<Integer>> opCapableMacList=new TreeMap<>();
		//一次是一个案例 MK1-10个案例
		for(int icase=0;icase<cases-3;icase++) {
			njobs=iomk.getNjob().get(icase);
			nmac=iomk.getNmac().get(icase);
			//sumop=iomk.getSumop().get(icase);
			nworker=iomw.getWorkersnumber().get(icase);
			nop=iomk.getNop().get(icase);
			meq=iomk.getMeq().get(icase);
			proc=iomk.getProc().get(icase);
			diata=iomk.getDiata().get(icase);	
			workerMachines=iomw.getWorkerMachines().get(icase);
			//还需要 1 每项工作生成操作数 2 为每项操作生成能执行的机器 3 为每项操作生成执行时间 4在后为每项操作生成实际执行时间
			//1每项工作生成操作数
			sumop=0;
			Random rand=new Random();
				for(int n=0;n<njobs;n++) {
					int op=0;
					if(nop[1]==nop[0])
						op=nop[0];
					else 
						op=rand.nextInt(nop[1]-nop[0]+1)+nop[0];
					sumop+=op;
					opList.add(op);
				}
				
			/*
			 * 为每项操作生成能执行的机器
			 * opCapableMacList key:opID value:能执行机器IDs
			 */
			
			for(int n=1;n<=sumop;n++) {
				List<Integer> macs=new ArrayList<>();
				for(int k=0;k<meq;k++) {
					int mac = 0;
					while(mac==0) {
						mac=rand.nextInt(nmac+1);//保证可取上界
					}
					macs.add(mac);
					}
				opCapableMacList.put(n, macs);
				}
			/*
			 * 3为每项操作生成能执行的机器
			 * index=opID-1
			 */
			for(int n=1;n<=sumop;n++) {
				int proctime=0;
				if(proc[1]==proc[0])
					proctime=proc[0];
				else 
					proctime=rand.nextInt(proc[1]-proc[0]+1)+proc[0];
				 procTimeList.add(proctime);
			}
			/*
			 * 4每项操作的执行时间波动范围生成
			 * index=opID-1
			 */
			for(int n=1;n<=sumop;n++) {
				int diataT=0;
				if(diata[1]==diata[0])
					diataT=diata[0];
				else 
					diataT=rand.nextInt(diata[1]-diata[0]+1)+diata[0];
				diataList.add(diataT);
			}
			//写案例文件
			writeCase(icase,njobs,nmac,nworker,sumop,meq,diata,opList,procTimeList,diataList,opCapableMacList, workerMachines);
		}
	}
	/*
	 * 创建实际调度案例 case
	 * task/operation resource  duration
	 */
	public void writeCase(int index,int njobs,int nmac,int nworker,int sumop,int meq,Integer[] diata,
			List<Integer> opList,List<Integer> procTimeList,List<Integer> diataList,
			Map<Integer,List<Integer>> opCapableMacList,Map<Integer,List<Integer>> workerMachines) {
		String name="MK"+(index+1);
		String file=".//DATA//"+"MK"+(index+1)+".txt";
		PrintWriter out=null;
			try {
				try {
					out=new PrintWriter(file,"UTF-8");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				System.out.println("UnsupportedEncoding");
			}
		//基本信息
		out.println(name);
		out.println("njobs"+" "+njobs);
		out.println("nmac"+" "+nmac);
		out.println("nworker"+" "+nworker);
		out.println("sumop"+" "+sumop);
		out.println("meq"+" "+meq);
		out.println("jobID"+"  "+"ops");
		for(int i=0;i<njobs;i++) {
			int jobid=i+1;
			out.print(jobid+" "+opList.get(i));
			out.println();
		}
		writeTask(out,njobs,opList,opCapableMacList);
		Map<Integer,List<Integer>> res=writeResource(out,nmac,nworker,workerMachines);
		writeDuration(out,nmac,nworker,sumop,procTimeList,diataList,opCapableMacList,res);
		out.close();
	}
	/*
	 * 写Task
	 * @param out 输出文件对象
	 * @param njobs 工作数
	 * @param opList 操作数 list
	 * @param opCapableMacList Map 操作ID-能执行机器IDs
	 */
	public void writeTask(PrintWriter out,int njobs,List<Integer>opList,Map<Integer,List<Integer>>opCapableMacList) {

		out.println("TASK");
		out.println("TASKID  "+"  CapableMachine"+"  Predecessors");
		int opS=0;
		/*
		 * opCapableMacList key:操作ID 
		 * opList.get(i) i工作的操作数
		 */
		for(int i=0;i<njobs;i++) {
			for(int k=0;k<opList.get(i);k++) {
				//输出 TaskID 能执行的机器数  紧前工作
				int taskid=k+1+opS;
				out.print(taskid+" "+opCapableMacList.get(taskid));
				for(int j=1+opS;j<taskid;j++) {
					out.print(" "+j);
				}
				out.println("");
			}
		   opS+=opList.get(i);
		}
	}
	/*
	 * 输出资源
	 * @param out 输出文件对象
	 * @param nmac 工作机器数
	 * @param nworker 工人数
	 * @param workerMachines Map key:workID value:能操作机器IDs list
	 * 输出资源信息 资源ID MachineID WorkerID
	 * exits 如果工人能操作对应的机器，则为true
	 */
	public Map<Integer,List<Integer>> writeResource(PrintWriter out,int nmac,int nworker,Map<Integer,List<Integer>>workerMachines) {
		out.println("Resource");
		out.println("resourceID  "+"MachineID  "+"WorkerID  "+"exits");
		Map<Integer,List<Integer>> res=new TreeMap<>();
		int resS=0;
		for(int i=0;i<nmac;i++) {
			int machineid=i+1;
			for(int j=0;j<nworker;j++) {
				List<Integer> macAndWorkerAndexits=new ArrayList<>();
				int workerid=j+1;
				int resID=j+1+resS;
				int exits=workerMachines.get(workerid).contains(machineid)==true?1:0;
				out.println(resID+" "+machineid+" "+workerid+" "+exits);
				macAndWorkerAndexits.add(machineid);
				macAndWorkerAndexits.add(workerid);
				macAndWorkerAndexits.add(exits);
				res.put(resID,macAndWorkerAndexits);
			}
			resS+=nworker;
		}
		return res;
	}
	/*	
	 * 写执行时间 
	 * @param out 输出文件对象
	 * @param nmac 每项案例的机器数
	 * @param nworker 工人数量
	 * @param sumop 所有工作的操作总和
	 * @param procTimeList 操作执行时间list
	 * @param opCapableMacList Map 操作ID-能执行机器IDs
	 * @param diataList 每项操作的波动时间diata list
	 * 输出操作时间  工人能操作机器 机器能执行操作 才有执行时间
	 * 结构 ResourceID operationID 二维数组 资源能执行操作则有操作时间  否则为-1 二维数组索引比对应的机器工人ID小1
	 */
	public void writeDuration(PrintWriter out,int nmac,int nworker,int sumop,
			List<Integer>procTimeList,List<Integer>diataList,
			Map<Integer,List<Integer>>opCapableMacList,Map<Integer,List<Integer>> res) {
		out.println("Duration");
		out.print("procTime  ");
		for(int k=1;k<=sumop;k++)
			out.print(procTimeList.get(k-1)+"  ");
		out.println("");
		out.print("diata  ");
		for(int k=1;k<=sumop;k++)
			out.print(diataList.get(k-1)+"  ");
		out.println("");
		out.print("ResID/OpID");
		for(int k=1;k<=sumop;k++)
			out.print(k+"  ");
		out.println("");
		int numRes=nmac*nworker;
		Integer[][]resop=new Integer[numRes][sumop];
		for(int i=0;i<numRes;i++) {
			for(int j=0;j<sumop;j++) {
				resop[i][j]=-1;
			}
		}
		Random rand=new Random();
		for(int i=0;i<numRes;i++) {
			int resID=i+1;
			out.print(resID+"     ");
			for(int j=0;j<sumop;j++) {
				int opID=j+1;
				//资源能执行操作 操作的资源集
				List<Integer> opmacs=opCapableMacList.get(opID);
				//"MachineID  "+"WorkerID  "+"exits"
				int mac=res.get(resID).get(0);
				int exit=res.get(resID).get(2);
				if(exit==1&&opmacs.contains(mac)) {
					resop[resID-1][opID-1]=procTimeList.get(opID-1)+rand.nextInt(diataList.get(opID-1));
				}
				out.print(" "+resop[resID-1][opID-1]);
			}
			out.println("");
		}
		
	}

}

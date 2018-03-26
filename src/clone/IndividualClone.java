package knowledgefruitflyfuzzy;

import java.util.ArrayList;
import java.util.List;

public class Individual {

	// 个体染色体的维数
	static final int chromosomeLayer = 2;
	// 每个个体的目标函数个数
	static final int objNum = 1;
	// 个体中任务
	private List<ITask> taskslist = new ArrayList<ITask>(); 
	//
	private List<Machine> reslist = new ArrayList<Machine>(); 
	// 染色体
	private List<List<Integer>> chromosome = new ArrayList<List<Integer>>();
	
	// 染色体随机数
	private List<List<Double>> chromosomeDNA = new ArrayList<List<Double>>();	
	// 目标函数
	private List<Integer> obj=new ArrayList<>();

	private Case project;
	//采用分配资源和安排机器上执行顺序一起的方法
	public Individual(Case project) {
		this.project = project;
		settaskslist(project);
		setResList(project);
		//随机产生DNA及任务序列、资源序列  随机可以更改 可以用其他启发式规则初始化
		deciphering( project);
		//计算个体的目标函数值，输出计算了起停时间的任务对象list
		objCompute(project);

	}
	public Individual(List<List<Integer>> _chromosome,Case project) {
		// 创建个体的染色体
		this.project = project;
		settaskslist(project);	
		setResList(project);
		this.chromosome = _chromosome;
		//this.chromosomeDNA = _chromosomemDNA;

		//计算个体的目标函数值，输出计算了起停时间的任务对象list
		objCompute(project);		
	}
		
	
	/*
	 * 十台机器 机器的完成时间初始化为0 
	 */
	private void setResList(Case project2) {
		// TODO Auto-generated method stub
		for (int i = 0; i < project.getResources().size();i++){
			Machine res = new Machine(project.getResources().get(i));
			reslist.add(res);
		}
	}
	/**
	 * 每个chromosome解密之后对应的目标函数值，用一个一维数组表示，数组长度等于目标函数的个数
	 * 
	 * @param chromosome
	 *            解密后的解
	 * @return 计算好起停时间的任务队列
	 */
	public void objCompute(Case project) {
		List<Task> tasks = project.getTasks();
		List<Integer> maxtime = new ArrayList<>();
		List<List<Integer>> finishTimes=new ArrayList<>();
		
		//下一步计算 目标最大完成时间根据机器的最后完成时间
		for (int i = 0; i < project.getNtask(); i++){
			List<Integer> preEndtime =new ArrayList<>();
			for(int k=0;k<3;k++) {
				preEndtime.add(0);
			}
			Task curtask = tasks.get(chromosome.get(0).get(i)-1);
			//得到所有前置任务,循环每一前置任务，取最晚结束时间
			List<Integer>  pretaskids = curtask.getPresecessorIDs();
			for (int j = 0; j < pretaskids.size();j++){
				List<Integer> finishTimeTemp=taskslist.get(pretaskids.get(j)-1).getFinishTime();//有问题
				preEndtime=Tools.computeStartTime(finishTimeTemp,preEndtime);
			}
			//当前任务所对应的资源最晚时间
			List<Integer> resFinish=reslist.get(chromosome.get(1).get(i)-1).getFinishTime();
			//计算开始时间
			
			List<Integer> startTime=Tools.computeStartTime(resFinish,preEndtime);/////////////////////
			//设置当前任务的开始时间及完成时间
			taskslist.get(chromosome.get(0).get(i)-1).setstarttime(startTime);
			//List<List<Integer>> process=tasks.get(chromosome.get(0).get(i)-1).getProcessTime();
			//System.out.println(process);
			List<Integer> processTime=tasks.get(chromosome.get(0).get(i)-1).getProcessTime().get(chromosome.get(1).get(i)-1);
			//System.out.print("StartTime  "+startTime);
			//System.out.print("  processTime"+processTime);
			//System.out.println("");
			List<Integer> completedTime=Tools.computeCompletedTime(startTime, processTime);
			taskslist.get(chromosome.get(0).get(i)-1).setFinishTime(completedTime);
			//更新当前任务资源的最后完工时间
			reslist.get(chromosome.get(1).get(i)-1).setFinishTime(completedTime);
		}
        //当前个体最后的完成时间
		for(int i=0;i<reslist.size();i++) {
			finishTimes.add(reslist.get(i).getFinishTime());
		}
		maxtime=Tools.computeMaxTime(finishTimes);
		this.obj=maxtime;
	}

	private void settaskslist(Case project){
		for (int i = 0; i < project.getTasks().size();i++){
			ITask itask = new ITask(project.getTasks().get(i));
			taskslist.add(itask);
		}
	}
	/**
	 * 将随机初始化解，解密成整数向量表示任务序列、资源序列的染色体结构
	 * 
	 * @param _chromosome
	 *            随机数组成的二维数组
	 * @return 返回由任务执行序列和资源分配序列组成的集合
	 */
	public void deciphering(Case project) {
		
		List<Integer> taskList = new ArrayList<Integer>();
		List<Integer> resourceList = new ArrayList<Integer>();
		// 可执行任务集合
		List<Integer> executableTaskIDS = new ArrayList<Integer>();	
		List<Task> tasks = project.getTasks();
        
		List<Double> _list1 = new ArrayList<>();
		List<Double> _list2 = new ArrayList<>();

		// 具体解密细节，需要补充

		// 求taskList任务执行序列和resourceList资源分配序列
		for (int i = 0; i < project.getNtask(); i++) {  
			
			executableTaskIDS.clear();
			double rand1 = Math.random();
			double rand2 = Math.random();
			_list1.add(rand1);
			_list2.add(rand2);
			
			for (int k = 0; k < tasks.size(); k++) {
				if (taskslist.get(k).pretasknum == 0){
					executableTaskIDS.add(tasks.get(k).getTaskID());
				}
			}
			if (executableTaskIDS.size() == 0){
				break;
			}
			int A = (int) ( rand1 * executableTaskIDS.size());
			int currentTaskID = executableTaskIDS.get(A);
			taskList.add(currentTaskID);
			taskslist.get(currentTaskID -1).pretasknum = -1;   //当前任务已经被使用，做上标记以防止下次被选用
			
			//处理后续任务
			for (int k = 0; k < tasks.size(); k++) {
				//把所有以任务j为前置任务的前置任务数减1；
				if (tasks.get(k).getPresecessorIDs().contains(currentTaskID)){
					taskslist.get(k).pretasknum--;	
				}
			}
			
			// 求对应的资源分配序列resourceList
			// 可执行该任务的资源集合
			List<Integer> list = tasks.get(currentTaskID -1).getResourceIDs();
			int B = (int) (rand2 * list.size());
			int resourceid = list.get(B);
			resourceList.add( resourceid );
		}
		this.chromosomeDNA.add(_list1);
		this.chromosomeDNA.add(_list2);
		this.chromosome.add(taskList);
		this.chromosome.add(resourceList);

		return ;
	}
	public List<ITask> getTaskslist() {
		return taskslist;
	}
	public void setTaskslist(List<ITask> taskslist) {
		this.taskslist = taskslist;
	}
	public List<Machine> getReslist() {
		return reslist;
	}
	public void setReslist(List<Machine> reslist) {
		this.reslist = reslist;
	}
	public List<List<Integer>> getChromosome() {
		return chromosome;
	}
	public void setChromosome(List<List<Integer>> chromosome) {
		this.chromosome = chromosome;
	}
	public List<Integer> getObj() {
		return obj;
	}
	public void setObj(List<Integer> obj) {
		this.obj = obj;
	}
	public Case getProject() {
		return project;
	}
	public void setProject(Case project) {
		this.project = project;
	}
	


}

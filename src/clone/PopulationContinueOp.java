package knowledgefruitflyfuzzy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Population {

	private int populationsize;
	private Individual[] population;
	private List<List<Integer>> populationObj;
	private Case project;
	public Population(int populationSize, Case project) {
		this.populationsize = populationSize;
		this.project = project;
		this.population = new Individual[populationSize];
	}
	
	public Population(int populationSize, Case project,boolean initial) {
		this.populationsize = populationSize;
		this.project = project;
		this.population = new Individual[populationSize];
		if (initial) {
			for (int i = 0; i < populationSize; i++) {
				Individual individual = new Individual(project);
				this.population[i] = individual;
			}
			this.populationObj = populationObjCompute(this.population);
		}
	}
	/**
	 * 计算给定种群的个体目标函数
	 * 得到个体目标函数的集合
	 * @param population
	 *            种群
	 * @return populationObj 种群中个体目标函数集合
	 */
	public List<List<Integer>> populationObjCompute(Individual[] population) {
		List<List<Integer>> populationObj = new ArrayList<>();
		for (Individual individual : population) {
			populationObj.add(individual.getObj());
		}
		return populationObj;
	}
	/////NSFFA/////////////////////////////////////////////////////////////////////////

	/**
	 * 使用果蝇优化算法获得下一代种群
	 * 
	 * @param project
	 *            案例集
	 * @return 下一代种群
	 */
	public Population getOffSpring_NSFFA() {
	     //算法求解优化
		Population OffSpring = new Population(NSFFA.NS,project);
		
		// 基于气味搜索，每个个体生成S个个体，种群大小为NS*S
		Population p1 = this.smell_BasedSearch();
		// 将两个种群合并
		Population mp1 = merged(this,p1);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		Population p2 = mp1.slectPopulation(NSFFA.NS);
		
		 // 基于知识的搜索
		Population Q = p2.knowledge_BasedSearch();
		// 将两个种群合并
		Population mp2 = merged(p2,Q);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		OffSpring = mp2.slectPopulation(NSFFA.NS);

		return OffSpring;
	}
	/**
	 * 基于知识库搜索
	 * 
	 * @param population
	 * @return
	 */
	public Population knowledge_BasedSearch() {
		Population newPopulation = new Population(this.getPopulationsize(),project);
		// 选择NE个精英个体
		Population EPop = this.slectPopulation(NSFFA.NE);

		List<Task> tasks = project.getTasks();
		
		//资源分配概率更新 根据精英个体
		//更新资源分配概率 没有更新
		updateResourceProb(EPop);
		// 遍历种群的个体，利用轮盘赌法为每个个体的染色体任务序列重新分配资源,并进行交叉算子操作
		for (int i = 0; i < this.getPopulationsize(); i++) {
			Individual parent = this.getPopulation()[i];
			// 个体i的染色体结构
			List<List<Integer>> newChromosome = new ArrayList<>();
			List<List<Integer>> chromosome1 = parent.getChromosome();
			// 创建子个体的染色体对象 复制
			List<List<Integer>> resChromosome = new ArrayList<>();
			for (int m = 0; m < chromosome1.size(); m++) {
				List<Integer> list = new ArrayList<>();
				for (int n = 0; n < chromosome1.get(m).size(); n++) {
					list.add(chromosome1.get(m).get(n));
				}
				resChromosome.add(list);
			}

			for (int j = 0; j < resChromosome.get(0).size(); j++) {
				// 任务ID
				int tID = resChromosome.get(0).get(j);
				// 任务对象
				Task t = tasks.get(tID - 1);
				// 任务t的可执行资源集
				Map<Integer,Double> capapleResource = t.getCapableResource();
				// 利用轮盘赌法为任务t重新分配资源
				int reassignResourceID = selectResource(capapleResource);
				resChromosome.get(1).set(j, reassignResourceID);
			}

			// 从NE精英个体群中随机选择一个个体  任务序列调整
			int randIndex = (int) (Math.random() * EPop.getPopulationsize());
			Individual parent_2 = EPop.getPopulation()[randIndex];
			List<List<Integer>> chromosome2 = parent_2.getChromosome();

			// 随机选择两个位置，p,q,需要满足1<=p<q<=J-1      => 0.5*size<q-p<0.75*size
			int p, q;
			while (true) {
				int size=resChromosome.get(0).size();
				p = (int) (Math.random() * size);
				q = (int) (Math.random() * size);
				if (p < q) {
					break;
				}
			}
			//更改 将经过资源调整的染色体resChromosome的p-q段加入新个体染色体 将chromosome2的不属于resChromosome的部分加入新个体 满足紧前紧后关系
			
			List<List<Integer>> midium=new ArrayList<>();
			List<List<Integer>> start=new ArrayList<>();
			List<List<Integer>> end=new ArrayList<>();
			// 用来存储中间任务序列
			List<Integer> tID_List = new ArrayList<>();
			//紧前关系的检查
			List<Integer> ta =new ArrayList<>();
			List<Integer> res=new ArrayList<>();
			List<Integer> ta1 =new ArrayList<>();
			List<Integer> res1=new ArrayList<>();
			for (int j = p; j <= q; j++) {
				tID_List.add( resChromosome.get(0).get(j));
				ta.add( resChromosome.get(0).get(j));
				res.add( resChromosome.get(1).get(j));
			}
			midium.add(new ArrayList<>(ta));
			midium.add(new ArrayList<>(res));
			ta.clear();
			res.clear();
			for (int j = 0; j < chromosome2.get(0).size(); j++) {
				if (!tID_List.contains(chromosome2.get(0).get(j))) { 
					int taskid=chromosome2.get(0).get(j);
					List<Integer> preids=tasks.get(taskid-1).getPresecessorIDs();
					if(preids.size()==0) {
						double rand=Math.random();
						if(rand<=0.5) {
							ta.add(taskid);
							res.add(chromosome2.get(1).get(j));
						}
						else {
							ta1.add(taskid);
							res1.add(chromosome2.get(1).get(j));
						}
					}
					else {
						int flag=0;
						for(int k=0;k<preids.size();k++) {
							if(tID_List.contains(preids.get(k))) {
								flag=1;
								break;
							}
						}
						if(flag==1) {
							//tID_List包含task紧前 添加到后面
							ta1.add(taskid);
							res1.add(chromosome2.get(1).get(j));
						}
						else {
							ta.add(taskid);
							res.add(chromosome2.get(1).get(j));
						}
					}
			}
			}
			start.add(new ArrayList<>(ta));
			start.add(new ArrayList<>(res));
			end.add(new ArrayList<>(ta1));
			end.add(new ArrayList<>(res1));
			List<Integer> task=new ArrayList<>();
			List<Integer> ress=new ArrayList<>();
			for(int e=0;e<start.get(0).size();e++) {
				task.add(start.get(0).get(e));
				ress.add(start.get(1).get(e));
			}
			for(int e=0;e<midium.get(0).size();e++) {
				task.add(midium.get(0).get(e));
				ress.add(midium.get(1).get(e));
			}
			for(int e=0;e<end.get(0).size();e++) {
				task.add(end.get(0).get(e));
				ress.add(end.get(1).get(e));
			}
			newChromosome.add(task);
			newChromosome.add(ress);
			// 创建子个体
			Individual offspring = new Individual(newChromosome,project);
			newPopulation.setIndividual(i, offspring);
		}

		return newPopulation;
	}
    /*
     * 根据精英个体更新资源分配概率
     * @param EPop 精英个体种群
     * @return rp 更新过后的资源到任务的分配概率  key:资源编号 value:资源到本任务的分配概率  map对应一个任务
     */
	private void updateResourceProb(Population EPop) {
		// TODO Auto-generated method stub
		List<Task> tasks = project.getTasks();
		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			List<Integer> capapleResourceid = task.getResourceIDs();
			//key:资源编号  value:资源分配到任务的概率
			Map<Integer,Double> rp = tasks.get(i).getCapableResource();	
			
			double sumTemp_P = 0;
			for (int j = 0; j < capapleResourceid.size(); j++) {
				double temp_P = (1 - NSFFA.alpha) * rp.get(capapleResourceid.get(j))
						+ NSFFA.alpha * getISum(EPop, task.getTaskID(), capapleResourceid.get(j)) / NSFFA.NE;
				rp.replace(capapleResourceid.get(j), temp_P);
				sumTemp_P += temp_P;
			}
			for (int j = 0; j < capapleResourceid.size(); j++) {
				double P = rp.get(capapleResourceid.get(j)) / sumTemp_P;
				rp.replace(capapleResourceid.get(j), P);
			}
			tasks.get(i).setCapableResource(rp);
		}
		
	}

	// 选择
	// 从混合种群中，选择前N个个体
	public Population slectPopulation(int num) {
		// 创建新的种群
		Population newPopulation = new Population(num,project);

		// 混合种群进行快速排序 混合种群内部也进行了排序
		// 返回混合种群根据目标值进行排序的个体 
		List<Individual> indivIndex = Tools.popSort(this, project);//失效
		for(int i=0;i<num;i++) {
			newPopulation.setIndividual(i,indivIndex.get(i));
		}
		return newPopulation;
	}	
	/**
	 * 基于气味搜索方法 遍历输入种群中的每个个体，每个个体经过操作，生成S个子个体
	 * @param population
	 * @return
	 */
	public Population smell_BasedSearch() {
		int size=this.getPopulationsize();
		Population newPopulation = new Population(size* NSFFA.S,project );
		List<Individual> indivList = new ArrayList<>();
		// 遍历输入种群中的每个个体，每个个体经过操作，生成S个子个体
		Individual[] individuals = this.getPopulation();
		for (int i = 0; i <size; i++) {
			Individual individual = individuals[i];
			for (int j = 0; j < NSFFA.S; j++) {

				List<List<Integer>> offspringChromosome = new ArrayList<>();
				for (int m = 0; m < individual.getChromosome().size(); m++) {
					List<Integer> list = new ArrayList<>();
					for (int n = 0; n < individual.getChromosome().get(m).size(); n++) {
						list.add(individual.getChromosome().get(m).get(n));
					}
					offspringChromosome.add(list);
				}
				// 随机选择任务序列中的某个位置  对1个任务的资源重新选择  资源调整
				int index_t_1 = (int) (Math.random() * offspringChromosome.get(0).size());
				
				int taskID = offspringChromosome.get(0).get(index_t_1);
				Task task = project.getTasks().get(taskID - 1);
				
				List<Integer> capapleResource = task.getResourceIDs();
				double rd = Math.random() ;
				int index_capaple = (int) (rd * capapleResource.size());
				
				int resourceid = capapleResource.get(index_capaple);
				offspringChromosome.get(1).set(index_t_1, resourceid);
				//此处搜索策略  解决双重资源受限 插入调整  任务序列调整
				/*
				for(int k=0;k<size;k++) {
					int index_origin = (int) (Math.random() * offspringChromosome.get(0).size());
					int index_new = (int) (Math.random() * offspringChromosome.get(0).size());
					if(index_origin!=index_new ) {
						int taskID1 = offspringChromosome.get(0).get(index_origin);
						int resourceID1 = offspringChromosome.get(1).get(index_origin);
						offspringChromosome.get(0).remove(index_origin);
						offspringChromosome.get(1).remove(index_origin);
						if(index_origin>index_new) {
							offspringChromosome.get(0).add(index_new, taskID1);
							offspringChromosome.get(1).add(index_new, resourceID1);
						}
						else {
							offspringChromosome.get(0).add(index_new-1, taskID1);
							offspringChromosome.get(1).add(index_new-1, resourceID1);
							index_new=index_new-1;
						}
						//检查紧前关系 修正关系 位置排序  ID排序 一一对应   在原始project中taskID比index小1
						Task task1 = project.getTasks().get(taskID1 - 1);
						List<Integer> preIDs=task1.getPresecessorIDs();
						List<Integer> IDs=new ArrayList<>(preIDs);
						IDs.add(taskID1);
						List<Integer> indexs=new ArrayList<>();
						Map<Integer,Integer> taskRes=new TreeMap<>();
						taskRes.put(taskID1, resourceID1);//存储需要重新排列的任务资源信息
						indexs.add(index_new);//存储位置
						
						for(int m=0;m<preIDs.size();m++) {
							//根据preTaskID查找其在染色体中的位置
							 int index=offspringChromosome.get(0).indexOf(preIDs.get(m));
							indexs.add(index);
							taskRes.put(preIDs.get(m),offspringChromosome.get(1).get(index));
						}

						Collections.sort(IDs);
						Collections.sort(indexs);
						for(int x=0;x<IDs.size();x++) {
							offspringChromosome.get(0).set(indexs.get(x),IDs.get(x));
							offspringChromosome.get(1).set(indexs.get(x),taskRes.get(IDs.get(x)));
						}
					}
					
				}
				*/
				// 重复随机选择任务序列中的某个位置，直到两个相邻任务没有紧前任务关系， 做1次任务位置交换
				while (true) {
					int index_t_2 = (int) (Math.random() * offspringChromosome.get(0).size());
					if (index_t_2 != (offspringChromosome.get(0).size() - 1)) {
						
						int taskID1 = offspringChromosome.get(0).get(index_t_2);
						int resourceID1 = offspringChromosome.get(1).get(index_t_2);
						int taskID2 = offspringChromosome.get(0).get(index_t_2 + 1);
						int resourceID2 = offspringChromosome.get(1).get(index_t_2 + 1);

						Task task1 = project.getTasks().get(taskID1 - 1);
						Task task2 = project.getTasks().get(taskID2 - 1);

						if (!project.isPredecessor(task1, task2)) {
							// 交换两个位置上的任务编号以及资源编号
							offspringChromosome.get(0).set(index_t_2, taskID2);
							offspringChromosome.get(1).set(index_t_2, resourceID2);
							offspringChromosome.get(0).set(index_t_2 + 1, taskID1);
							offspringChromosome.get(1).set(index_t_2 + 1, resourceID1);
							break;
						} 
					}
				}
				
				// 创建子代个体对象
				Individual offspring = new Individual(offspringChromosome,project);
				indivList.add(offspring);
			}
		}
		for (int i = 0; i < indivList.size(); i++) {
			newPopulation.setIndividual(i, indivList.get(i));
		}
		return newPopulation;
	}
	/*
	 * 合并两个种群
	 */
	public Population merged(Population p1,Population p2){
		List<Individual> mergedList = new ArrayList<>();
		for (int i = 0; i < p1.getPopulationsize(); i++) {
			if (!mergedList.contains(p1.getPopulation()[i])) {
			  mergedList.add(p1.getPopulation()[i]);				
			}
		}
		for (int i = 0; i < p2.getPopulationsize(); i++) {
			if (!mergedList.contains(p2.getPopulation()[i])) {
				  mergedList.add(p2.getPopulation()[i]);				
				}			
		}
		Population mergedPopulation = new Population(mergedList.size(),project);
		for (int i =0; i <mergedList.size();i++){
			mergedPopulation.setIndividual(i, mergedList.get(i));
		}
		return mergedPopulation;
	}
	/**
	 * 计算在NE个精英个体中，有多少个体满足指定任务，被指定资源分配
	 * 
	 * @param ePop
	 * @param taskID
	 * @param resourceID
	 * @return
	 */
	private int getISum(Population ePop, int taskID, int resourceID) {
		int sum = 0;
		for (int i = 0; i < ePop.getPopulationsize(); i++) {
			Individual indiv = ePop.getPopulation()[i];
			// 指定任务在个体染色体中的索引
			int index_t = indiv.getChromosome().get(0).indexOf(taskID);
			int rID = indiv.getChromosome().get(1).get(index_t);
			// 如果与指定资源ID相同，sum加1
			if (rID == resourceID) {
				sum++;
			}
		}
		return sum;
	}
	/**
	 * 轮盘赌法，为每个任务重新分配资源
	 * @param capapleResource
	 * @return
	 */
	public int selectResource(Map<Integer, Double> capapleResource) {
		//double rouletteWheelPosition = Math.random();
		// 选择资源                =>选择最大的
		List<Map.Entry<Integer,Double>> capapleList=new ArrayList<>();
		for(Map.Entry<Integer,Double> entry:capapleResource.entrySet()) {
			capapleList.add(entry);
		}
		Collections.sort(capapleList,new Comparator<Map.Entry<Integer,Double>>(){

			@Override
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				// TODO Auto-generated method stub
				int flag=0;
				//根据分配概率大小进行排序 分配概率大的背选中
				if(o1.getValue()>o2.getValue()) {
					flag=-1;
				}
				if(o1.getValue()<o2.getValue()) {
					flag=1;
				}
				return flag;
			}
			
		});
		/*
		double spinWheel = 0;
		int Resourceid = 0;
		Iterator<Integer> rt = capapleResource.keySet().iterator();
		while(rt.hasNext()){
			Resourceid = rt.next();
			spinWheel += capapleResource.get(Resourceid);
			if (spinWheel >= rouletteWheelPosition) {
				break;
			}		
		}
		*/
		return capapleList.get(0).getKey();
	}

	// 设置种群中的个体
	public Individual setIndividual(int offset, Individual individual) {
		return population[offset] = individual;
	}
	public int getPopulationsize() {
		return populationsize;
	}

	public void setPopulationsize(int populationsize) {
		this.populationsize = populationsize;
	}

	public Individual[] getPopulation() {
		return population;
	}

	public void setPopulation(Individual[] population) {
		this.population = population;
	}

	public List<List<Integer>> getPopulationObj() {
		return populationObj;
	}

	public void setPopulationObj(List<List<Integer>> populationObj) {
		this.populationObj = populationObj;
	}

	public Case getProject() {
		return project;
	}

	public void setProject(Case project) {
		this.project = project;
	}
	
}

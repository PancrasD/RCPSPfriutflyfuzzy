package knowledgefruitfly;

public class CaseFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//读取文件
		String file=".//dic//Data.txt";
		RCPSPIOMK iomk=new RCPSPIOMK(file);
	    RCPSPIODP iodp=new RCPSPIODP(file);
	    WorkerMachineIO iomw=new WorkerMachineIO(file);
        //第二步 创建案例
	    CaseMK casemk=new CaseMK(iomk,iomw);
	}

}

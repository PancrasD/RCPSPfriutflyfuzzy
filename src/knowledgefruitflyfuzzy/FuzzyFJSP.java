package knowledgefruitflyfuzzy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class FuzzyFJSP {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length==1){
            File ff = new File("case_def");
            String[] fl = ff.list(); 
            if (fl==null){
            	System.out.print("没有在case_def目录下找到算例文件");
            	return;           	
            }
    		if (args[0].trim().toLowerCase().equals("f")){
    			PrintStream out = System.out;
                for(int i = 0; i<fl.length; i++){
                	 String _fn =  "case_def/" + fl[i];
                	 String _fo = "datalei/fuzzyFJSP_"+fl[i]+".txt";
                	 NSFFA_algorithm(_fn,_fo);
                }
                System.setOut(out);
                System.out.println(fl.length +"个案例算法计算完成"); 
                return;
                
    		}
    		
        }else{
        	System.out.print("请输入参数：'f'、自学习算法");
        	return;
        }

	}
	public static void NSFFA_algorithm(String casefile,String datafile) throws IOException{
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);

			// 初始化种群
			Population P = new Population(NSFFA.NS,project,true);
			
			int generationCount = 0;
	        //循环迭代 算法指定的次数
			while (generationCount < NSFFA.maxGenerations ) {

				P = P.getOffSpring_NSFFA();

				generationCount++;
			}
			//从最后得到种群中获取最优解集
			Population solutions = Tools.getbestsolution(P, project);
			
	
			 File f = new File(datafile);
			 PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   System.setOut(ps);
			   //输出最优解集
			   Tools.printsolutions(solutions,startTime);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }
	}

}

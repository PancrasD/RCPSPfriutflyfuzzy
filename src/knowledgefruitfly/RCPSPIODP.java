package knowledgefruitfly;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/*MK1-13 DP1-12
*@param njob 工作数
*@param nmac 机器数
*@param sumop 总操作数
*@param nop 每项工作操作数 范围
*@param proc 每项操作执行时间 范围
*@param p 机器能执行操作的概率 当能执行曹操作的机器数为空 则随机安排一台
*@param diata 每项操作执行时间波动范围值 
*/
public class RCPSPIODP{
	private List<Integer> njob;
	private List<Integer> nmac;
	private List<Integer> sumop;
	private List<Integer[]> nop;
	private List<Integer[]> proc;
	private List<Double>  p;
	private List<Integer[]> diata;
	public RCPSPIODP(String file)  {
		readData(file);
	}
	public void readData(String datafile)  {
		BufferedReader read=null;
		try {
			read=new BufferedReader(new FileReader(datafile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("filepath not exits ");
		}
		try {
			String line=read.readLine();
			this.skipTo(read,line,"DP1-12");
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
	private String skipTo(BufferedReader read,String line,String mark) throws IOException {
		while(null!=line&&!line.startsWith(mark)) {
			line=read.readLine();
		}
		return line;
	}
	private void readInitial(BufferedReader read) throws IOException {
	   String str=read.readLine();//这行舍弃
	   List<Integer> njob=new ArrayList<Integer>();
	   List<Integer> nmac=new ArrayList<Integer>();
	   List<Integer> sumop=new ArrayList<Integer>();
	   List<Integer[]> nop=new ArrayList<>();
	   List<Double> p=new ArrayList<Double>();
	   List<Integer[]> proc=new ArrayList<>();
	   List<Integer[]> diata=new ArrayList<>();
	   if(str.startsWith("dp")) {
		 while((!(str=read.readLine()).equals(""))) {
			 String[] strs=str.split(" +");
			 njob.add(Integer.parseInt(strs[1]));
			 nmac.add(Integer.parseInt(strs[2]));
			 sumop.add(Integer.parseInt(strs[3]));
			 
			 String[] nopS=strs[4].split("-");
			 Integer[] nopI= {Integer.parseInt(nopS[0]),Integer.parseInt(nopS[1])};
			 nop.add(nopI);

			 String[] procS=strs[5].split("-");
			 Integer[] procI= {Integer.parseInt(procS[0]),Integer.parseInt(procS[1])};
			 proc.add(procI);
			 //
			 p.add(Double.parseDouble(strs[7]));
			 
			 String[] diataS=strs[8].split("-");
			 Integer[] diataI= {Integer.parseInt(diataS[0]),Integer.parseInt(diataS[1])};
			 diata.add(diataI); 		 
		 }
	   }
	   this.setNjob(njob);
	   this.setNmac(nmac);
	   this.setSumop(sumop);
	   this.setNop(nop);
	   this.setProc(proc);
	   this.setP(p);
	   this.setDiata(diata);
	}
	public List<Integer> getNjob() {
		return njob;
	}
	public void setNjob(List<Integer> njob) {
		this.njob = njob;
	}
	public List<Integer> getNmac() {
		return nmac;
	}
	public void setNmac(List<Integer> nmac) {
		this.nmac = nmac;
	}
	public List<Integer> getSumop() {
		return sumop;
	}
	public void setSumop(List<Integer> sumop) {
		this.sumop = sumop;
	}
	public List<Integer[]> getNop() {
		return nop;
	}
	public void setNop(List<Integer[]> nop) {
		this.nop = nop;
	}
	public List<Integer[]> getProc() {
		return proc;
	}
	public void setProc(List<Integer[]> proc) {
		this.proc = proc;
	}
	public List<Double> getP() {
		return p;
	}
	public void setP(List<Double> p) {
		this.p = p;
	}
	public List<Integer[]> getDiata() {
		return diata;
	}
	public void setDiata(List<Integer[]> diata) {
		this.diata = diata;
	}
	
}


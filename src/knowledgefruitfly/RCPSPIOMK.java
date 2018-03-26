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
*@param meq 每项操作最多能被执行机器数 
*@param proc 每项操作执行时间 范围
*@param diata 每项操作执行时间波动范围值 
*/
public class RCPSPIOMK {
	private List<Integer> njob;
	private List<Integer> nmac;
	private List<Integer> sumop;
	private List<Integer[]> nop;
	private List<Integer> meq;
	private List<Integer[]> proc;
	private List<Integer[]> diata;
	public RCPSPIOMK(String file)  {
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
			this.skipTo(read,line,"MK1-13");
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
	   String str=read.readLine();//此行舍弃
	   List<Integer> njob=new ArrayList<Integer>();
	   List<Integer> nmac=new ArrayList<Integer>();
	   List<Integer> sumop=new ArrayList<Integer>();
	   List<Integer[]> nop=new ArrayList<>();
	   List<Integer> meq=new ArrayList<Integer>();
	   List<Integer[]> proc=new ArrayList<>();
	   List<Integer[]> diata=new ArrayList<>();
	   if(str.startsWith("mk")) {
		 while((!(str=read.readLine()).equals(""))) {
			 String[] strs=str.split(" +");
			 njob.add(Integer.parseInt(strs[1]));
			 nmac.add(Integer.parseInt(strs[2]));
			 sumop.add(Integer.parseInt(strs[3]));
			 
			 String[] nopS=strs[4].split("-");
			 Integer[] nopI= {Integer.parseInt(nopS[0]),Integer.parseInt(nopS[1])};
			 nop.add(nopI);
			 
			 meq.add(Integer.parseInt(strs[5]));
			 
			 String[] procS=strs[6].split("-");
			 Integer[] procI= {Integer.parseInt(procS[0]),Integer.parseInt(procS[1])};
			 proc.add(procI);
			 
			 String[] diataS=strs[7].split("-");
			 Integer[] diataI= {Integer.parseInt(diataS[0]),Integer.parseInt(diataS[1])};
			 diata.add(diataI); 		 
		 }
	   }
	   this.setNjob(njob);
	   this.setNmac(nmac);
	   this.setSumop(sumop);
	   this.setNop(nop);
	   this.setMeq(meq);
	   this.setProc(proc);
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
	public List<Integer> getMeq() {
		return meq;
	}
	public void setMeq(List<Integer> meq) {
		this.meq = meq;
	}
	public List<Integer[]> getProc() {
		return proc;
	}
	public void setProc(List<Integer[]> proc) {
		this.proc = proc;
	}
	public List<Integer[]> getDiata() {
		return diata;
	}
	public void setDiata(List<Integer[]> diata) {
		this.diata = diata;
	}
	
}

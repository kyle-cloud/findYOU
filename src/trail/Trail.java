package trail;

import java.util.ArrayList;
import java.util.Date;

import com.sun.xml.internal.bind.v2.model.core.ID;

public class Trail implements Cloneable{
	int sum_points = 0;
	ArrayList<Point> points = new ArrayList<>();
	private String ID = null;
	private String IMSI = null;
	private Date Tstart;
	private Date Tend;
	private double hm = 0;
	private int hm_index = -1;
	int cluster_id = 0;
	
	public Trail() {
		// TODO Auto-generated constructor stub
	}
	
	public int getSum_points() {
		return sum_points;
	}
	
	public ArrayList<Point> getPoints() {
		return points;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getIMSI() {
		return IMSI;
	}
	
	public Date getTstart() {
		return Tstart;
	}
	
	public Date getTend() {
		return Tend;
	}
	
	public double getHm() {
		return hm;
	}
	
	public int getHm_index() {
		return hm_index;
	}
	
	public int getCluster_id() {
		return cluster_id;
	}
	
	public void setSum_points(int sum_points) {
		this.sum_points = sum_points;
	}
	
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	
	public void setID(String iD) {
		ID = iD;
	}
	
	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}
	
	public void setTstart(Date tstart) {
		Tstart = tstart;
	}
	
	public void setTend(Date tend) {
		Tend = tend;
	}
	
	public void setHm(double hm) {
		this.hm = hm;
	}
	
	public void setHm_index(int hm_index) {
		this.hm_index = hm_index;
	}
	
	public void setCluster_id(int cluster_id) {
		this.cluster_id = cluster_id;
	}
	
	//把这个方法重写一下就行，什么都不写
    @Override
	public Object clone() throws CloneNotSupportedException {
     
        return super.clone();
    }
}

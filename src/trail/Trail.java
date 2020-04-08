package trail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.bson.types.ObjectId;

public class Trail implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	int sum_points = 0;
	ArrayList<Point> points = new ArrayList<>();
	private ObjectId ID = null;
	private ObjectId trail_id = null;
	private String IMSI = null;
	private long tstart;
	private long tend;
	private double hm = 0;
	private int hm_index = -1;
	int cluster_id = 0;
	int test = 0;
	double score = 0.0;
	
	public Trail() {
		// TODO Auto-generated constructor stub
	}
	
	public int getSum_points() {
		return sum_points;
	}
	
	public ArrayList<Point> getPoints() {
		return points;
	}
	
	public ObjectId getID() {
		return ID;
	}
	
	public String getIMSI() {
		return IMSI;
	}
	
	public long getTstart() {
		return tstart;
	}
	
	public long getTend() {
		return tend;
	}
	
	public double getHm() {
		return hm;
	}
	
	public int getTest() {
		return test;
	}
	
	public int getHm_index() {
		return hm_index;
	}
	
	public int getCluster_id() {
		return cluster_id;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setSum_points(int sum_points) {
		this.sum_points = sum_points;
	}
	
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	
	public void setID(ObjectId iD) {
		this.ID = iD;
	}
	
	public void setIMSI(String iMSI) {
		this.IMSI = iMSI;
	}
	
	public void setTstart(long start) {
		this.tstart = start;
	}
	
	public void setTend(long end) {
		this.tend = end;
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
	
	public void setTest(int test) {
		this.test = test;
	}
	
	public void setScore(double score) {
		this.score = (double) Math.round(score * 10000) / 10000;
	}
	
	@Override
	public Trail clone() {
		// TODO Auto-generated method stub
		Trail trail = null;
 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			trail = (Trail) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trail;
	}

	public ObjectId getTrail_id() {
		return trail_id;
	}

	public void setTrail_id(ObjectId trail_id) {
		this.trail_id = trail_id;
	}
}

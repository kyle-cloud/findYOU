package trail;

import java.sql.Date;
import java.util.ArrayList;

public class Trail {
	ArrayList<Point> points = null;
	private Date Tstart;
	private Date Tend;
	
	public Trail() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<Point> getPoints() {
		return points;
	}
	
	public Date getTstart() {
		return Tstart;
	}
	
	public Date getTend() {
		return Tend;
	}
	
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	
	public void setTstart(Date tstart) {
		Tstart = tstart;
	}
	
	public void setTend(Date tend) {
		Tend = tend;
	}
}

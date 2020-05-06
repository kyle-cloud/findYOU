package trail;

import org.bson.types.ObjectId;

public class Line {
	Point start_point = new Point();
	Point end_point = new Point();
	ObjectId trail_id = null;
	int cid = 0;
	boolean isVisited = false;
	
	public Point getStart_point() {
		return start_point;
	}
	
	public Point getEnd_point() {
		return end_point;
	}
	
	public ObjectId getTrail_id() {
		return trail_id;
	}
	
	public int getCid() {
		return cid;
	}
	
	public boolean isVisited() {
		return isVisited;
	}
	
	public void setStart_point(Point start_point) {
		this.start_point = start_point;
	}
	
	public void setEnd_point(Point end_point) {
		this.end_point = end_point;
	}
	
	public void setTrail_id(ObjectId trail_id) {
		this.trail_id = trail_id;
	}
	
	public void setCid(int cluster_id) {
		this.cid = cluster_id;
	}
	
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
}

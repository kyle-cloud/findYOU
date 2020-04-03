package trail;

import java.util.ArrayList;

import org.bson.types.ObjectId;

public class fineTrail {
	ObjectId ID = null;
	ObjectId trail_id = null;
	int cluster_id = 0;
	ArrayList<Trail> trails = new ArrayList<>();
	
	public ArrayList<Trail> getTrails() {
		return trails;
	}
	
	public ObjectId getID() {
		return ID;
	}
	
	public ObjectId getTrail_id() {
		return trail_id;
	}
	
	public int getCluster_id() {
		return cluster_id;
	}
	
	public void setTrails(ArrayList<Trail> trails) {
		this.trails = trails;
	}
	
	public void setID(ObjectId iD) {
		this.ID = iD;
	}
	
	public void setTrail_id(ObjectId trail_id) {
		this.trail_id = trail_id;
	}
	
	public void setCluster_id(int cluster_id) {
		this.cluster_id = cluster_id;
	}
}

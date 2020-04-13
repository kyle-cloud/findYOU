package process;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import calculation.calculations;
import dao.MongoUtil;
import test.test;
import trail.Point;
import trail.Trail;

public class cluster {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ArrayList<Trail> dividedTrail = new ArrayList<>();
		ArrayList<Point> coarseTrail = new ArrayList<>();
		ArrayList<Trail> finTrails = new ArrayList<>();
		ArrayList<Trail> cluseredTrails = new ArrayList<>();
		try {
			ArrayList<Trail> testTrails = downloadData.getTrails("testTrail_coarse");
			ArrayList<Trail> trainTrails = downloadData.getTrails("trail_coarse");
			finTrails.addAll(testTrails);
			finTrails.addAll(trainTrails);
			
			cluseredTrails = calculations.structCluster(finTrails, 0.8, 0.88, 150);
			
//			MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail");
//			for(int i = 0; i < testTrails.size(); i ++) {
//				coll.updateOne(Filters.eq("_id", testTrails.get(i).getTrail_id()), new Document("$set", new Document("Cluster_id", finTrails.get(i).getCluster_id())));
//			}
//			
//			coll = MongoUtil.instance.getCollection("liu", "trainTrail");
//			for(int i = testTrails.size(); i < finTrails.size(); i ++) {
//				coll.updateOne(Filters.eq("_id", trainTrails.get(i).getTrail_id()), new Document("$set", new Document("Cluster_id", finTrails.get(i).getCluster_id())));
//			}
			for(int i = 0; i < finTrails.size(); i ++) {
				System.out.println(finTrails.get(i).getCluster_id());
			}
			System.out.println("¾ÛÀàÍê³É");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package process;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import calculation.calculations;
import dao.MongoUtil;
import trail.Point;
import trail.Trail;

public class cluster {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ArrayList<Trail> dividedTrail = new ArrayList<>();
		ArrayList<Point> coarseTrail = new ArrayList<>();
		ArrayList<Trail> finTrails = new ArrayList<>();
		ArrayList<Integer> cluseredTrails = new ArrayList<>();
		try {
			ArrayList<Object> result_test = downloadData.getTrails_coarse("testTrail_coarse");
			ArrayList<Trail> testTrails = (ArrayList<Trail>) result_test.get(0);
			ArrayList<ObjectId> testTrails_parent = (ArrayList<ObjectId>) result_test.get(1);
			ArrayList<Object> result_train = downloadData.getTrails_coarse("trail_coarse");
			ArrayList<Trail> trainTrails = (ArrayList<Trail>) result_train.get(0);
			ArrayList<ObjectId> trainTrails_parent = (ArrayList<ObjectId>) result_train.get(1);
			finTrails.addAll(testTrails);
			finTrails.addAll(trainTrails);
			
			cluseredTrails = calculations.structCluster(finTrails, 0.8, 0.88, 3000);
			
			MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail");
			for(int i = 0; i < testTrails.size(); i ++) {
				coll.updateOne(Filters.eq("_id", testTrails_parent.get(i)), new Document("$set", new Document("Cluster_id", finTrails.get(i).getCluster_id())));
			}
			
			coll = MongoUtil.instance.getCollection("liu", "trainTrail");
			for(int i = testTrails.size(); i < finTrails.size(); i ++) {
				coll.updateOne(Filters.eq("_id", trainTrails_parent.get(i)), new Document("$set", new Document("Cluster_id", finTrails.get(i).getCluster_id())));
			}
			System.out.println("¾ÛÀàÍê³É");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

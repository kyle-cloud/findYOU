package process;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import calculation.calculations;
import dao.MongoUtil;
import trail.Point;
import trail.Trail;

public class cluster {
	public static void main(String[] args) {
		ArrayList<Trail> dividedTrail = new ArrayList<>();
		ArrayList<Point> coarseTrail = new ArrayList<>();
		ArrayList<Trail> finTrails = new ArrayList<>();
		ArrayList<Integer> cluseredTrails = new ArrayList<>();
		try {
			ArrayList<Trail> testTrails = downloadData.getTrails("testTrail");
			ArrayList<Trail> trainTrails = downloadData.getTrails("trail");
			ArrayList<Trail> trails = new ArrayList<>();
			trails.addAll(testTrails);
			trails.addAll(trainTrails);
			System.out.println("共有" + trails.size() + "条轨迹");
			for(int i = 0; i < trails.size(); i ++) {
				dividedTrail = calculations.divideTrace(trails.get(i), 420*60*1000);
				coarseTrail = calculations.coarseCompress(dividedTrail);
				Trail coarse_finTrail = new Trail();
				coarse_finTrail.setTest(trails.get(i).getTest());
				coarse_finTrail.setID(trails.get(i).getID());
				coarse_finTrail.setIMSI(trails.get(i).getIMSI());
				coarse_finTrail.setPoints(coarseTrail);
				coarse_finTrail.setSum_points(coarseTrail.size());
				coarse_finTrail.setTstart(coarseTrail.get(0).getDate());
				coarse_finTrail.setTend(coarseTrail.get(coarseTrail.size()-1).getDate());
				finTrails.add(coarse_finTrail);
			}
			cluseredTrails = calculations.structCluster(finTrails, 0.8, 0.88, 3000);
			MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail");
			for(int i = 0; i < testTrails.size(); i ++) {
				coll.updateOne(Filters.eq("_id", finTrails.get(i).getID()), new Document("$set", new Document("Cluster_id", finTrails.get(i).getCluster_id())));
			}
			coll = MongoUtil.instance.getCollection("liu", "trail");
			for(int i = testTrails.size(); i < finTrails.size(); i ++) {
				coll.updateOne(Filters.eq("_id", finTrails.get(i).getID()), new Document("$set", new Document("Cluster_id", finTrails.get(i).getCluster_id())));
			}
			System.out.println("聚类完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package process;

import java.util.ArrayList;
import java.util.Comparator;

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
			
			long startTime = System.currentTimeMillis();
			cluseredTrails = calculations.structCluster(finTrails, 0.8, 0.88, 2000);
			long endTime = System.currentTimeMillis();
			System.out.println("原始运行时间：" + (endTime - startTime) + "ms");
//			MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail");
//			for(int i = 0; i < testTrails.size(); i ++) {
//				coll.updateOne(Filters.eq("_id", testTrails.get(i).getTrail_id()), new Document("$set", new Document("Cluster_id", finTrails.get(i).getCluster_id())));
//			}
//			
//			coll = MongoUtil.instance.getCollection("liu", "trainTrail");
//			for(int i = testTrails.size(); i < finTrails.size(); i ++) {
//				coll.updateOne(Filters.eq("_id", trainTrails.get(i).getTrail_id()), new Document("$set", new Document("Cluster_id", finTrails.get(i).getCluster_id())));
//			}
//			for(int i = 0; i < finTrails.size(); i ++) {
//				System.out.println(finTrails.get(i).getCluster_id());
//			}
			System.out.println("噪声轨迹数目：" + cluseredTrails.size());
			
			finTrails.sort(new Comparator<Trail>() {
	            @Override
	            public int compare(Trail t1, Trail t2) {
	            	if(t1.getCluster_id() > t2.getCluster_id())
	    				return 1;
	            	else if(t1.getCluster_id() == t2.getCluster_id())
	            		return 0;
	    			return -1;
	            }
	        });
			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			int cur = finTrails.get(0).getCluster_id();
			int count = 1;
			for(int i = 1; i < finTrails.size(); i ++) {
				if(cur == finTrails.get(i).getCluster_id()) {
					count ++;
				} else {
					min = Math.min(min, count);
					max = Math.max(max, count);
					cur = finTrails.get(i).getCluster_id();
					count = 1;
				}
			}
			System.out.println("最小簇中轨迹数：" + min);
			System.out.println("最大簇中轨迹数：" + max);
			System.out.println("聚类完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

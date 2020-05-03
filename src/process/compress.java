package process;

import java.util.ArrayList;
import java.util.Date;

import org.bson.BSONObject;
import org.bson.Document;

import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;

import calculation.calculations;
import dao.MongoUtil;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import trail.Point;
import trail.Trail;

public class compress {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		ArrayList<Trail> dividedTrail = new ArrayList<>();
		ArrayList<Point> coarseTrail = new ArrayList<>();
		ArrayList<Trail> fineTrail = new ArrayList<>();

		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail_coarse");
		MongoCollection<Document> coll1 = MongoUtil.instance.getCollection("liu", "testTrail_fine");
		ArrayList<Trail> trails = downloadData.getTrails("testTrail");
		ArrayList<Long> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			dividedTrail = calculations.divideTrace(trails.get(i), 480*60*1000);
			coarseTrail = calculations.coarseCompress(dividedTrail);
			ArrayList<Object> result = trailToArrays(coarseTrail);
			dates = (ArrayList<Long>) result.get(0);
			longitudes = (ArrayList<Double>) result.get(1);
			latitudes = (ArrayList<Double>) result.get(2);
			Document document = new Document();
			document.put("IMSI", trails.get(i).getIMSI());
			document.put("trail_id", trails.get(i).getID());
			document.put("cluster_id", trails.get(i).getCluster_id());
			document.put("tracetimes", dates);
			document.put("longitudes", longitudes);
			document.put("latitudes", latitudes);
			document.put("test", 1);
			coll.insertOne(document);
			
			dividedTrail = calculations.divideTrace(trails.get(i), 480*60*1000);
			fineTrail = calculations.fineCompress(dividedTrail, 5000, (long)120*60*1000);
			result = trailToArrays(getAllPointsInFine(fineTrail));
			dates = (ArrayList<Long>) result.get(0);
			longitudes = (ArrayList<Double>) result.get(1);
			latitudes = (ArrayList<Double>) result.get(2);
			document = new Document();
			document.put("IMSI", trails.get(i).getIMSI());
			document.put("trail_id", trails.get(i).getID());
			document.put("cluster_id", trails.get(i).getCluster_id());
			document.put("tracetimes", dates);
			document.put("longitudes", longitudes);
			document.put("latitudes", latitudes);
			document.put("test", 0);
			coll1.insertOne(document);

			System.out.println(i);
		}
	}
	
	public static ArrayList<Point> getAllPointsInFine(ArrayList<Trail> finetrails) {
		ArrayList<Point> points = new ArrayList<>();
		for(int i = 0; i < finetrails.size(); i ++) {
			points.addAll(finetrails.get(i).getPoints());
		}
		return points;
	}
	
	public static ArrayList<Object> trailToArrays(ArrayList<Point> points) {
		ArrayList<Object> result = new ArrayList<>();
		ArrayList<Long> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		for(int i = 0; i < points.size(); i ++) {
			dates.add(points.get(i).getDate());
			longitudes.add(points.get(i).getLng());
			latitudes.add(points.get(i).getLat());
		}
		result.add(dates);
		result.add(longitudes);
		result.add(latitudes);
		return result;
	}
}

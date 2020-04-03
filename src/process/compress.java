package process;

import java.util.ArrayList;
import java.util.Date;

import org.bson.BSONObject;
import org.bson.Document;

import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;

import calculation.calculations;
import dao.JsonDateValueProcessor;
import dao.MongoUtil;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import trail.Point;
import trail.Trail;

public class compress {
	public static void main(String[] args) throws Exception {
		ArrayList<Trail> dividedTrail = new ArrayList<>();
		ArrayList<Point> coarseTrail = new ArrayList<>();
		ArrayList<Trail> fineTrail = new ArrayList<>();
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail_coarse");
		MongoCollection<Document> coll1 = MongoUtil.instance.getCollection("liu", "trail_fine");
		ArrayList<Trail> trails = downloadData.getTrails("trail");
		for(int i = 0; i < trails.size(); i ++) {
			dividedTrail = calculations.divideTrace(trails.get(i), 420*60*1000);
			coarseTrail = calculations.coarseCompress(dividedTrail);
			Trail coarse_finTrail = new Trail();
			
			coarse_finTrail.setID(trails.get(i).getID());
			coarse_finTrail.setIMSI(trails.get(i).getIMSI());
			coarse_finTrail.setPoints(coarseTrail);
			coarse_finTrail.setSum_points(coarseTrail.size());
			coarse_finTrail.setTstart(coarseTrail.get(0).getDate());
			coarse_finTrail.setTend(coarseTrail.get(coarseTrail.size()-1).getDate());
	
			Document document = new Document();
			net.sf.json.JSONObject jObject = net.sf.json.JSONObject.fromObject(coarse_finTrail);
			document.put("trail_id", trails.get(i).getID());
			document.put("trail", jObject);
			coll.insertOne(document);
			
			dividedTrail = calculations.divideTrace(trails.get(i), 420*60*1000);
			fineTrail = calculations.fineCompress(dividedTrail, 0.03, (long)1000000);
			
			Trail fine_finTrail = new Trail();
			ArrayList<Point> points = getAllPointsInFine(fineTrail);
			fine_finTrail.setID(trails.get(i).getID());
			fine_finTrail.setIMSI(trails.get(i).getIMSI());
			fine_finTrail.setPoints(points);
			fine_finTrail.setSum_points(points.size());
			fine_finTrail.setTstart(points.get(0).getDate());
			fine_finTrail.setTend(points.get(points.size()-1).getDate());
			
			document = new Document();
			jObject = net.sf.json.JSONObject.fromObject(fine_finTrail);
			document.put("cluster_id", trails.get(i).getCluster_id());
			document.put("trail_id", trails.get(i).getID());
			document.put("trail", jObject);
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
}

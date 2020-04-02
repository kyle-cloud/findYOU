package process;

import java.util.ArrayList;

import org.bson.BSONObject;
import org.bson.Document;

import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;

import calculation.calculations;
import dao.MongoUtil;
import net.sf.json.JSONArray;
import trail.Point;
import trail.Trail;

public class compress {
	public static void main(String[] args) throws Exception {
		ArrayList<Trail> dividedTrail = new ArrayList<>();
		ArrayList<Point> coarseTrail = new ArrayList<>();
		ArrayList<Trail> fineTrail = new ArrayList<>();
		
		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail_coarse");
		MongoCollection<Document> coll1 = MongoUtil.instance.getCollection("liu", "testTrail_fine");
		ArrayList<Trail> trails = downloadData.getTrails("testTrail");
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
			document.put("Trail_id", trails.get(i).getID());
			document.put("Trail", jObject);
			coll.insertOne(document);
			
			dividedTrail = calculations.divideTrace(trails.get(i), 420*60*1000);
			fineTrail = calculations.fineCompress(dividedTrail, 0.03, (long)1000000);
			
			document = new Document();
			JSONArray jsonArray = JSONArray.fromObject(fineTrail);
			document.put("Trail_id", trails.get(i).getID());
			document.put("Trail", jsonArray);
			coll1.insertOne(document);
			System.out.println(i);
		}
	}
}

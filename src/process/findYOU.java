package process;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import calculation.calculations;
import dao.MongoUtil;
import net.sf.json.JSONArray;
import trail.Point;
import trail.Trail;

public class findYOU {
	public static void main(String[] args){
		ArrayList<Trail> dividedTrail = new ArrayList<>();
		ArrayList<Point> coarseTrail = new ArrayList<>();
		ArrayList<Trail> finTrails = new ArrayList<>();
		ArrayList<Trail> cluseredTrails = new ArrayList<>();
		try{
//			MongoUtil.instance.dropCollection("liu", "coarseTrail");
			//获取轨迹数据
			ArrayList<Trail> trails = downloadData.getTrails();
			System.out.println("共有" + trails.size() + "条轨迹");
			//分时段，并粗粒度降维
			for(int i = 0; i < trails.size(); i ++) {
				dividedTrail = calculations.divideTrace(trails.get(i), 120*60*1000);
				coarseTrail = calculations.coarseCompress(dividedTrail);
				Trail coarse_finTrail = new Trail();
				coarse_finTrail.setIMSI(trails.get(i).getIMSI());
				coarse_finTrail.setPoints(coarseTrail);
				coarse_finTrail.setSum_points(coarseTrail.size());
				coarse_finTrail.setTstart(coarseTrail.get(0).getDate());
				coarse_finTrail.setTend(coarseTrail.get(coarseTrail.size()-1).getDate());
				finTrails.add(coarse_finTrail);

//				Document document = new Document();
//				net.sf.json.JSONObject jObject = net.sf.json.JSONObject.fromObject(coarse_finTrail);
//				document.put("Trail", jObject);
//				MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "coarseTrail");
//				coll.insertOne(document);
//				coarse_dividedTrail = calculations.divideTrace(trails.get(i), 120*60*1000);
//细粒度降维		fineTrail = calculations.fineCompress(dividedTrail, 0.03, (long)1000000);
				
//				Document document = new Document();
//				JSONArray jsonArray = JSONArray.fromObject(fineTrail);
//				document.put("IMSI", trails.get(i).getIMSI());
//				document.put("Trail", jsonArray);
//				MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "fineTrail");
//				coll.insertOne(document);
			}
			cluseredTrails = calculations.structCluster(finTrails, finTrails.get(0), 0.9, 0.88, 50);
			
			ArrayList<Trail> objTrail = calculations.divideTrace(finTrails.get(0), 120*60*1000);
			ArrayList<Trail> objFineTrail = calculations.fineCompress(objTrail, 0.03, (long)1000000);
			
			ArrayList<Trail> cmpTrail = calculations.divideTrace(cluseredTrails.get(59), 120*60*1000);
			ArrayList<Trail> cmpFineTrail = calculations.fineCompress(cmpTrail, 0.03, (long)1000000);
			System.out.println(calculations.innerSimilarity(objFineTrail, cmpFineTrail));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

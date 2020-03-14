package process;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import calculation.calculations;
import dao.MongoUtil;
import net.sf.json.JSONArray;
import sun.security.timestamp.TSRequest;
import trail.Point;
import trail.Trail;

public class findYOU {
	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		ArrayList<Trail> dividedTrail = new ArrayList<>();
		ArrayList<Point> coarseTrail = new ArrayList<>();
		ArrayList<Trail> finTrails = new ArrayList<>();
		ArrayList<Integer> cluseredTrails = new ArrayList<>();
		try{
//			MongoUtil.instance.dropCollection("liu", "coarseTrail");
			//获取轨迹数据
			ArrayList<Trail> trails = downloadData.getTrails("trail");
			System.out.println("共有" + trails.size() + "条轨迹");
			//分时段，并粗粒度降维
			for(int i = 0; i < trails.size(); i ++) {
				dividedTrail = calculations.divideTrace(trails.get(i), 420*60*1000);
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
//				coarse_dividedTrail = calculations.divideTrace(trails.get(i), 420*60*1000);
//细粒度降维		fineTrail = calculations.fineCompress(dividedTrail, 0.03, (long)1000000);
				
//				Document document = new Document();
//				JSONArray jsonArray = JSONArray.fromObject(fineTrail);
//				document.put("IMSI", trails.get(i).getIMSI());
//				document.put("Trail", jsonArray);
//				MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "fineTrail");
//				coll.insertOne(document);
			}
			cluseredTrails = calculations.structCluster(finTrails, finTrails.get(0), 0.9, 0.88, 50);
			Trail temp = (Trail)trails.get(0).clone();
			ArrayList<Trail> objTrail = calculations.divideTrace(trails.get(0), 420*60*1000);
			ArrayList<Trail> objFineTrail = calculations.fineCompress(objTrail, 0.03, (long)1000000);
			ArrayList<Object> result_topTrails_indexes = calculations.findTopk(objFineTrail, 1);
			//objFineTrail = (ArrayList<Trail>)result_topTrails_indexes.get(0);
			
			ArrayList<Trail> cmpTrail = calculations.divideTrace(temp, 420*60*1000);
			ArrayList<Trail> cmpFineTrail = calculations.fineCompress(cmpTrail, 0.03, (long)1000000);
			
			for(int i = 0; i < objTrail.get(0).getPoints().size(); i ++) {
				System.out.println(objTrail.get(0).getPoints().get(i).getLng() + " " + cmpTrail.get(0).getPoints().get(i).getLng());
			}
			for(int i = 0; i < objFineTrail.get(0).getPoints().size(); i ++) {
				System.out.println(objFineTrail.get(0).getPoints().get(i).getLng() + " " + cmpFineTrail.get(0).getPoints().get(i).getLng());
			}
			
			//ArrayList<Integer> objTopIndexs = (ArrayList<Integer>)result_topTrails_indexes.get(1); //找到目标轨迹提取的片段下标
			//cmpFineTrail = calculations.getTopk(cmpFineTrail, objTopIndexs);
			
			
			System.out.println(calculations.innerSimilarity(objFineTrail, cmpFineTrail));

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

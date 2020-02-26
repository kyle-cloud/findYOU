package process;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import calculation.calculations;
import dao.MongoUtil;
import trail.Point;
import trail.Trail;

public class findYOU {
	public static void main(String[] args){
		ArrayList<Trail> dividedTrail = new ArrayList<>();
		ArrayList<Point> coarseTrail = new ArrayList<>();
		ArrayList<Trail> finTrails = new ArrayList<>();
		try{
//			MongoUtil.instance.dropCollection("liu", "coarseTrail");
			//获取轨迹数据
			ArrayList<Trail> trails = downloadData.getTrails();
			System.out.println("共有" + trails.size() + "条轨迹");
			//分时段，并粗粒度降维
			for(int i = 0; i < trails.size(); i ++) {
				dividedTrail = calculations.divideTrace(trails.get(i), 60*60*1000);
				coarseTrail = calculations.coarseCompress(dividedTrail);
				Trail finTrail = new Trail();
				finTrail.setIMSI(trails.get(i).getIMSI());
				finTrail.setPoints(coarseTrail);
				finTrail.setSum_points(coarseTrail.size());
				finTrail.setTstart(coarseTrail.get(0).getDate());
				finTrail.setTend(coarseTrail.get(coarseTrail.size()-1).getDate());
				finTrails.add(finTrail);
				
//				Document document = new Document();
//				net.sf.json.JSONObject jObject = net.sf.json.JSONObject.fromObject(finTrail);
//				document.put("Trail", jObject);
//				MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "coarseTrail");
//				coll.insertOne(document);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

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
		try{
//			MongoUtil.instance.dropCollection("liu", "coarseTrail");
			//获取轨迹数据
			ArrayList<Trail> trails = downloadData.getTrails();
			System.out.println("共有" + trails.size() + "条轨迹");
			//分时段，并粗粒度降维
			for(int i = 0; i < trails.size(); i ++) {
				dividedTrail = calculations.divideTrace(trails.get(i), 60*60*1000);
				coarseTrail = calculations.coarseCompress(dividedTrail);
				Document document = new Document();
				JSONArray jsonArray = JSONArray.fromObject(coarseTrail);
				document.put("Trail", jsonArray);
				MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "coarseTrail");
				coll.insertOne(document);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package dao;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import calculation.calculations;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import trail.Trail;


public class Util {
//	public <T> T toBean(Document document,Class<T> clzss){
//		System.out.println("2222222");
//		response.setCharacterEncoding("utf-8");
//		PrintWriter respWritter = response.getWriter();
//		ArrayList<Trail> trails = new ArrayList<>();
//		ArrayList<Trail> finTrails = new ArrayList<>();
//		ArrayList<Integer> empty = new ArrayList<>();
//	    try {
//	    	//添加测试集
//	    	Bson bson = Filters.eq("IMSI", IMSI);
//	    	ArrayList<Trail> testTrails = findFirstTrailsByFilter(bson, "testTrail");
//	    	
//	    	if(testTrails.size() == 0) {
//	    		empty.add(0);
//	    		JSONArray data = JSONArray.fromObject(empty);
//	            respWritter.append(data.toString());
//	            return;
//	    	}
////	    	System.out.println("333");
////	    	bson = Filters.eq("Cluster_id", testTrails.get(0).getCluster_id());
////	    	ArrayList<Trail> trainTrails = findTrailsByFilter(bson, "trail");
////	    	if(trainTrails.size() == 0) {
////	    		empty.add(1);
////	    		JSONArray data = JSONArray.fromObject(empty);
////	            respWritter.append(data.toString());
////	            return;
////	    	}
////	    	
////	    	trails.addAll(testTrails);
////	    	trails.addAll(trainTrails);
//	    	
//	    	System.out.println("111");
//	    	MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail_fine");
//			Document document = coll.find(Filters.eq("Trail_id", testTrails.get(0).getID())).first();
//			ArrayList<Trail> objFineTrail = (ArrayList<Trail>) document.get("Trail");
//			ArrayList<Object> result_topTrails_indexes = calculations.findTopk(objFineTrail, 1);
//			objFineTrail = (ArrayList<Trail>)result_topTrails_indexes.get(0);
//			
//			coll = MongoUtil.instance.getCollection("liu", "trail_fine");
//			double min = Integer.MAX_VALUE;
//			double max = 0.0;
//			for(int i = testTrails.size(); i < trails.size(); i ++) {
//				document = coll.find(Filters.eq("Trail_id", trails.get(0).getID())).first();
//				ArrayList<Trail> cmpFineTrail = (ArrayList<Trail>) document.get("Trail");
//				ArrayList<Integer> objTopIndexs = (ArrayList<Integer>)result_topTrails_indexes.get(1);
//				cmpFineTrail = calculations.getTopk(cmpFineTrail, objTopIndexs);
//				double temp = calculations.innerSimilarity(objFineTrail, cmpFineTrail);
//				trails.get(i).setScore(temp);
//				min = Math.min(min, temp);
//				max = Math.max(max, temp);
//			}
//			for(int i = testTrails.size(); i < trails.size(); i ++) {
//				trails.get(i).setScore((1 - (trails.get(i).getScore() - min) / (max - min)));
//			}
//			trails.sort(new Comparator<Trail>() {
//				 @Override
//				 public int compare(Trail t1, Trail t2) {
//					 if(t1.getScore() < t2.getScore())
//						 return 1;
//					 else if(t1.getScore() == t2.getScore())
//						 return 0;
//					 else
//						 return -1;
//				 }
//			});
//			int count = 0;
//			for(int i = testTrails.size(); count < 10 && i < trails.size(); i ++) {
//				count ++;
//				finTrails.add(trails.get(i));
//			}
//            /*将list集合装换成json对象*/
//            JSONArray data = JSONArray.fromObject(finTrails);
//            respWritter.append(data.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//	}
}

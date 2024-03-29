package process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import dao.MongoUtil;
import trail.Point;
import trail.Trail;

public class downloadData {
	@SuppressWarnings("unchecked")
	public static ArrayList<Trail> getTrails(String collname) {
		ArrayList<Trail> trails = new ArrayList<>();
		ArrayList<Point> points = new ArrayList<>();
		ObjectId ID = null;
		String IMSI = null;
		int Test = 0;
		ArrayList<Long> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		try {
			MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", collname);
			MongoCursor<Document> cursor = coll.find().iterator();
			int total = 0;
			while(total < 20000 && cursor.hasNext()) {
				total ++;
				Document document = cursor.next();
				
				ID = document.getObjectId("_id");
				IMSI = (String)document.get("IMSI");
				Test = (int)document.get("test");
				dates = (ArrayList<Long>) document.get("tracetimes");
				longitudes = (ArrayList<Double>) document.get("longitudes");
				latitudes = (ArrayList<Double>) document.get("latitudes");
//				System.out.println(longitudes.get(0));
//				System.out.println(dates.get(0));
//				System.out.println(latitudes.get(0));
				for(int i = 0; i < dates.size(); i ++) {
					Point point = new Point();
					point.setDate(dates.get(i));
					point.setLng(longitudes.get(i));
					point.setLat(latitudes.get(i));
					//point.setCor(); 哦哦，这是原始轨迹，得等粗粒度降维的时候在计算转角，敲上瘾了-_-!
					points.add(point);
				}
				Trail trail = new Trail();
				trail.setSum_points(points.size());
				trail.setPoints((ArrayList<Point>)points.clone());
				trail.setID(ID);
				trail.setIMSI(IMSI);
				trail.setTest(Test);
				trail.setTstart(points.get(0).getDate());
				trail.setTend(points.get(dates.size()-1).getDate());
				trails.add(trail);
				//清空
				points.clear();
				dates.clear();
				longitudes.clear();
				latitudes.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trails;
	}
	
	public static ArrayList<Object> getTrails_coarse(String collname) {
		ArrayList<Object> result = new ArrayList<>();
		ArrayList<Trail> trails = new ArrayList<>();
		ArrayList<ObjectId> ID_parents = new ArrayList<>();
		
		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", collname);
		MongoCursor<Document> cursor = coll.find().iterator();
		while(cursor.hasNext()) {
			Document document = cursor.next();
			trails.add((Trail)document.get("trail"));
		}
		result.add(trails);
		result.add(ID_parents);
		return result;
	}
	
	public static void main(String[] args) {
		ArrayList<Trail> trails = new ArrayList<>();
		trails = getTrails("trail");
		for(int i = 0; i < 5; i ++) {
			System.out.println(trails.get(i).getTstart());
			System.out.println(trails.get(i).getTstart());
		}
	}
}

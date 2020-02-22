package process;

import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import dao.MongoUtil;
import trail.Point;
import trail.Trail;

public class downloadData {
	@SuppressWarnings("unchecked")
	public static ArrayList<Trail> downloadData() {
		ArrayList<Trail> trails = new ArrayList<>();
		ArrayList<Point> points = new ArrayList<>();
		String IMSI = null;
		ArrayList<Date> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		try {
			MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail");
			MongoCursor<Document> cursor = coll.find().iterator();
			while(cursor.hasNext()) {
				Document document = cursor.next();
				IMSI = (String)document.get("IMSI"); 
				dates = (ArrayList<Date>) document.get("TraceTimes");
				longitudes = (ArrayList<Double>) document.get("Longitudes");
				latitudes = (ArrayList<Double>) document.get("Latitudes");
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
				trail.setPoints(points);
				trail.setIMSI(IMSI);
				trail.setTstart(dates.get(0));
				trail.setTend(dates.get(dates.size()-1));
				trails.add(trail);
				//清空
				points.clear();
				dates.clear();
				longitudes.clear();
				latitudes.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MongoUtil.instance.close();
		}
		return trails;
	}
	
	public static void main(String[] args) {
		System.out.println(downloadData().get(0));
	}
}

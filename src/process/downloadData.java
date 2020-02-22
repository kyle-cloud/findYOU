package process;

import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import dao.MongoUtil;
import trail.Point;
import trail.Trail;

public class downloadData {
	@SuppressWarnings("unchecked")
	public static ArrayList<Trail> main(String[] args) {
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
					point.setCor();
					points.add(point);
				}
				Trail trail = new Trail();
				trail.setPoints(points);
				trail.setIMSI(IMSI);
				trail.setTstart(dates.get(0));
				trail.setTend(dates.get(dates.size()-1));
				trails.add(trail);
				//Çå¿Õ
				points.clear();
				dates.clear();
				longitudes.clear();
				latitudes.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return trails;
			MongoUtil.instance.close();
		}
	}
}

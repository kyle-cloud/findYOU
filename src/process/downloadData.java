package process;

import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import dao.MongoUtil;

public class downloadData {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ArrayList<Date> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		try {
			MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail");
			MongoCursor<Document> cursor = coll.find().iterator();
			if(cursor.hasNext()) {
				Document document = cursor.next();
				dates = (ArrayList<Date>) document.get("TraceTimes");
				longitudes = (ArrayList<Double>) document.get("Longitudes");
				latitudes = (ArrayList<Double>) document.get("Latitudes");
				System.out.println(longitudes.get(0));
				System.out.println(dates.get(0));
				System.out.println(latitudes.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MongoUtil.instance.close();
		}
	}
}

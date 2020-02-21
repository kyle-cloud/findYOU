package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import dao.MongoUtil;

public class processData {
	static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void readFile(String path) {
		File file = new File(path);
		BufferedReader bReader = null;
		ArrayList<String> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		try {
			bReader = new BufferedReader(new FileReader(file));
			for(String line = new String(bReader.readLine().getBytes(), "utf-8"); line != null; line = bReader.readLine()) {
				String[] strs = line.split(",");
				dates.add(strs[1]);
				longitudes.add(Double.parseDouble(strs[2]));
				latitudes.add(Double.parseDouble(strs[3]));
			}
			Document document = new Document();
			document.put("Date", dates);
			document.put("longitude", longitudes);
			document.put("latitude", latitudes);
			MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail");
			coll.insertOne(document);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\1.txt");
		
//		查看时间格式		
//		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail");
//		MongoCursor<Document> sd =coll.find().iterator();
//        while(sd.hasNext()) {
//        	Document doc = sd.next();
//        	ArrayList<String> result = (ArrayList<String>)doc.get("Date");
//        	System.out.println((Date)format.parse(result.get(0)));
//        }
        MongoUtil.instance.close();
	}
}

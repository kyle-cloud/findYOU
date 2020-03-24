package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import dao.MongoUtil;

public class uploadData {
	public static void readFile(String path) throws UnsupportedEncodingException, IOException {
		File file = new File(path);
		BufferedReader bReader = null;
		String IMSI = null;
		ArrayList<String> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		
		
		
		try {
			bReader = new BufferedReader(new FileReader(file));
			bReader.mark((int)file.length() + 1);
				
			//特别注意把太小的文件直接当一条轨迹
			if(file.length() < 4000) return;
			int divided_nums = (int) (file.length() / 4000);
			//System.out.println(file.length() + " : " + divided_nums);
			for(int i = 0; i < divided_nums; i ++) {
				for(int j = 0; j < i; j++) {
					bReader.readLine();
				}
				dates.clear();
				longitudes.clear();
				latitudes.clear();
				for(String line = new String(bReader.readLine().getBytes(), "utf-8"); line != null; line = bReader.readLine()) {
					for(int j = 0; j < divided_nums - 1; j ++) {
						if(bReader.readLine() != null);
						else break;
					}
					String[] strs = line.split(",");
					IMSI = strs[0];
					dates.add(strs[1]);
					longitudes.add(Double.parseDouble(strs[2]));
					latitudes.add(Double.parseDouble(strs[3]));
				}
				Document document = new Document();
				document.put("IMSI", IMSI);
				document.put("TraceTimes", dates);
				document.put("Longitudes", longitudes);
				document.put("Latitudes", latitudes);
				MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail");
				coll.insertOne(document);
				bReader.reset();
			}
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
	
	public static void main(String[] args) throws Exception {
		for(int i = 1; i < 10357; i ++) {
			System.out.println(i);
			readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\" + String.valueOf(i) +".txt");
		}
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\10.txt");
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\16.txt");
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\28.txt");
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\34.txt");
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\35.txt");
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\131.txt");
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\490.txt");
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\799.txt");
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\820.txt");
//		readFile("D:\\Trail_Data\\taxi_log_2008_by_id\\839.txt");
		
//		查看时间格式		
//		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail");
//		MongoCursor<Document> sd =coll.find().iterator();
//        while(sd.hasNext()) {
//        	Document doc = sd.next();
//        	ArrayList<String> result = (ArrayList<String>)doc.get("Date");
//        	System.out.println((Date)format.parse(result.get(0)));
//        }
	}
}

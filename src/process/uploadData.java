package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import dao.MongoUtil;

public class uploadData {
	public static void readFile(String path) throws UnsupportedEncodingException, IOException {
		File file = new File(path);
		BufferedReader bReader = null;
		String IMSI = null;
		ArrayList<Long> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		
		
		try {
			bReader = new BufferedReader(new FileReader(file));
			bReader.mark((int)file.length() + 1);
				
			int flag = 1;
			//�ر�ע���̫С���ļ�ֱ�ӵ�һ���켣
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
					dates.add(df.parse(strs[1]).getTime());
					longitudes.add(Double.parseDouble(strs[2]));
					latitudes.add(Double.parseDouble(strs[3]));
				}
				Document document = new Document();
				document.put("IMSI", IMSI);
				document.put("cluster_id", 0);
				document.put("trail_id", null);
				document.put("tracetimes", dates);
				document.put("longitudes", longitudes);
				document.put("latitudes", latitudes);
				MongoCollection<Document> coll;
				if(flag == 1) {
					coll = MongoUtil.instance.getCollection("liu", "testTrail");
					document.put("test", 1);
					flag = 0;
				}
				else {
					coll = MongoUtil.instance.getCollection("liu", "trail");
					document.put("test", 0);
				}
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
//		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail");
//		coll.update({}, {$set: {'Cluster_id': 0}}, {multi: true});
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
		
//		�鿴ʱ���ʽ		
//		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail");
//		MongoCursor<Document> sd =coll.find().iterator();
//        while(sd.hasNext()) {
//        	Document doc = sd.next();
//        	ArrayList<String> result = (ArrayList<String>)doc.get("Date");
//        	System.out.println((Date)format.parse(result.get(0)));
//        }
	}
}

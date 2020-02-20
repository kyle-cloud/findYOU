package process;

import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;

import dao.MongoUtil;

public class processData {
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void readFile(String path) {
		File file = new File(path);
		BufferedReader bReader = null;
		ArrayList<Date> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		try {
			bReader = new BufferedReader(new FileReader(file));
			for(String line = new String(bReader.readLine().getBytes(), "utf-8"); line != null; line = bReader.readLine()) {
				BasicDBObject basicDBObject = new BasicDBObject();
				String[] strs = line.split(",");
				dates.add((Date) format.parse(strs[1]));
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
				MongoUtil.instance.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

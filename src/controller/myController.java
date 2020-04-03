package controller;


import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import calculation.calculations;
import dao.JsonDateValueProcessor;
import dao.MongoUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import process.cluster;
import process.downloadData;
import sun.invoke.empty.Empty;
import trail.Point;
import trail.Trail;
import trail.fineTrail;

@Controller
public class myController {
	static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@RequestMapping("/findTheTrail")
	@ResponseBody
	public void findTheTrail(String IMSI, HttpServletResponse response) throws IOException{
	    ArrayList<Trail> trails = new ArrayList<>();
	    try {
	    	Bson bson = Filters.eq("IMSI", IMSI);
	    	//添加测试集
	    	ArrayList<Trail> testTrails = findTrailsByFilter(bson, "testTrail");
	    	ArrayList<Trail> trainTrails = findTrailsByFilter(bson, "trail");
	    	trails.addAll(testTrails);
	    	trails.addAll(trainTrails);
            /*将list集合装换成json对象*/
	    	Gson gson = new Gson();
            String data = gson.toJson(trails);
            //接下来发送数据
            /*设置编码，防止出现乱码问题*/
            response.setCharacterEncoding("utf-8");
            /*得到输出流*/
            PrintWriter respWritter = response.getWriter();
            /*将JSON格式的对象toString()后发送*/
            respWritter.append(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/findTheSimilarity")
	@ResponseBody
	public void findTheSimilarity(String IMSI, HttpServletResponse response) throws IOException {
		System.out.println("传值成功111");
		response.setCharacterEncoding("utf-8");
		Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

		PrintWriter respWritter = response.getWriter();
		ArrayList<Trail> trails = new ArrayList<>();
		ArrayList<Trail> finTrails = new ArrayList<>();
		ArrayList<Integer> empty = new ArrayList<>();
	    try {
	    	//添加测试集
	    	Bson bson = Filters.eq("IMSI", IMSI);
	    	ArrayList<Trail> testTrails = findFirstTrailsByFilter(bson, "testTrail");
	    	
	    	if(testTrails.size() == 0) {
	    		empty.add(0);
	    		String data = gson.toJson(empty);
	            respWritter.append(data.toString());
	            return;
	    	}
	    	
	    	System.out.println("导入一条测试轨迹成功222");
	    	bson = Filters.eq("Cluster_id", testTrails.get(0).getCluster_id());
	    	ArrayList<Trail> trainTrails = findTrailsByFilter(bson, "trail");
	    	if(trainTrails.size() == 0) {
	    		empty.add(1);
	    		JSONArray data = JSONArray.fromObject(empty);
	            respWritter.append(data.toString());
	            return;
	    	}
	    	
	    	trails.addAll(testTrails);
	    	trails.addAll(trainTrails);
	    	
	    	java.lang.reflect.Type listType = new TypeToken<ArrayList<Trail>>() {}.getType();
	    	System.out.println("导入训练轨迹成功333");
	    	MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail_fine");
			Document document = coll.find(Filters.eq("Trail_id", testTrails.get(0).getID())).first();
			Object object = document.get("Trail");
			ArrayList<Trail> objFineTrail = new ArrayList<>();
			String jString = gson.toJson(object).toString();
			objFineTrail =  gson.fromJson(jString, listType);
//			JSONArray jArray = JSONArray.fromObject(object);
//			for(int i = 0; i < jArray.size(); i ++) {
//				JSONObject jsonObject = JSONObject.fromObject(jArray.getJSONObject(i));
//				objFineTrail.add(gson.fromJson(gson.toJson(jsonObject), Trail.class));
//			}
			
			ArrayList<Object> result_topTrails_indexes = calculations.findTopk(objFineTrail, 1);
			objFineTrail = (ArrayList<Trail>)result_topTrails_indexes.get(0);
			ArrayList<Integer> objTopIndexs = (ArrayList<Integer>)result_topTrails_indexes.get(1);
			
			System.out.println("测试细粒度轨迹解析成功444");
			coll = MongoUtil.instance.getCollection("liu", "trail_fine");
			double min = Integer.MAX_VALUE;
			double max = 0.0;
			for(int i = testTrails.size(); i < trails.size(); i ++) {
				document = coll.find(Filters.eq("Trail_id", trails.get(i).getID())).first();
				object = document.get("Trail");
				ArrayList<Trail> cmpFineTrail = new ArrayList<>();
				jString = gson.toJson(object).toString();
				cmpFineTrail =  gson.fromJson(jString, listType);
				
				cmpFineTrail = calculations.getTopk(cmpFineTrail, objTopIndexs);
				double temp = calculations.innerSimilarity(objFineTrail, cmpFineTrail);
				trails.get(i).setScore(temp);
				min = Math.min(min, temp);
				max = Math.max(max, temp);
				System.out.println(i);
			}
			System.out.println("训练细粒度成功555");
			
			for(int i = testTrails.size(); i < trails.size(); i ++) {
				trails.get(i).setScore((1 - (trails.get(i).getScore() - min) / (max - min)));
			}
			trails.sort(new Comparator<Trail>() {
				 @Override
				 public int compare(Trail t1, Trail t2) {
					 if(t1.getScore() < t2.getScore())
						 return 1;
					 else if(t1.getScore() == t2.getScore())
						 return 0;
					 else
						 return -1;
				 }
			});
			int count = 0;
			for(int i = testTrails.size(); count < 10 && i < trails.size(); i ++) {
				count ++;
				finTrails.add(trails.get(i));
			}
            /*将list集合装换成json对象*/
			String data = gson.toJson(finTrails);
            respWritter.append(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Trail> findFirstTrailsByFilter(Bson bson, String collection) throws ParseException {
		ObjectId ID = null;
		int Test = 0;
		String IMSI = null;
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<Long> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		ArrayList<Trail> trails = new ArrayList<>();
    	MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", collection);
		Document document = coll.find(bson).first();
		if(document == null) {
			return trails;
		}
		ID = document.getObjectId("_id");
		Test = (int)document.get("Test");
		IMSI = (String)document.get("IMSI");
		dates = (ArrayList<Long>) document.get("TraceTimes");
		longitudes = (ArrayList<Double>) document.get("Longitudes");
		latitudes = (ArrayList<Double>) document.get("Latitudes");
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
    	return trails;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Trail> findTrailsByFilter(Bson bson, String collection) throws ParseException {
		ObjectId ID = null;
		int Test = 0;
		String IMSI = null;
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<Long> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		ArrayList<Trail> trails = new ArrayList<>();
		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", collection);
		MongoCursor<Document> cursor = coll.find(bson).iterator();
    	while(cursor.hasNext()) {
    		Document document = cursor.next();
			ID = document.getObjectId("_id");
			Test = (int)document.get("Test");
			IMSI = (String)document.get("IMSI");
			dates = (ArrayList<Long>) document.get("TraceTimes");
			longitudes = (ArrayList<Double>) document.get("Longitudes");
			latitudes = (ArrayList<Double>) document.get("Latitudes");
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
    	return trails;
	}
	
	public static ArrayList<fineTrail> getFineTrails(Bson bson) {
		Gson gson = new Gson();
		ArrayList<fineTrail> fineTrails = new ArrayList<>();
		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail_fine");
		MongoCursor<Document> cursor = coll.find(bson).iterator();
    	while(cursor.hasNext()) {
    		Document document = cursor.next();
    		Object object = document.get("Trail");
			String jString = gson.toJson(object).toString();
			fineTrails.add(gson.fromJson(jString, fineTrail.class));
    	}
    	return fineTrails;
	}
}
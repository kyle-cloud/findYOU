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
import dao.MongoUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import process.cluster;
import process.downloadData;
import sun.invoke.empty.Empty;
import trail.Point;
import trail.Trail;

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
	    	
	    	Gson gson = new Gson();
            String data = gson.toJson(trails);
            response.setCharacterEncoding("utf-8");
            PrintWriter respWritter = response.getWriter();
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
		Gson gson = new Gson();

		PrintWriter respWritter = response.getWriter();
		ArrayList<Trail> trails = new ArrayList<>();
		ArrayList<Trail> finTrails = new ArrayList<>();
		ArrayList<Integer> empty = new ArrayList<>();
	    try {
	    	//添加测试集
	    	Bson bson = Filters.eq("IMSI", IMSI);
	    	Trail testTrail = findFirstTrailsByFilter(bson, "testTrail");
	    	if(testTrail.getID() == null) {
	    		empty.add(0);
	    		String data = gson.toJson(empty);
	            respWritter.append(data.toString());
	            return;
	    	}
	    	System.out.println("导入测试轨迹成功222");
	    	
	    	long startTime = System.currentTimeMillis();
	    	//导出所有此cluster_id的细粒度轨迹
	    	bson = Filters.eq("cluster_id", testTrail.getCluster_id());
	    	trails.addAll(findTrailsByFilter(bson, "trail_fine"));
	    	long endTime = System.currentTimeMillis();
	    	System.out.println("导入训练细粒度轨迹成功333:" + (endTime - startTime) + "ms");//13660
	    	
	    	//根据信息熵提取重要轨迹
	    	ArrayList<Trail> objFineTrail = new ArrayList<>();
	    	bson = Filters.eq("trail_id", testTrail.getID());
	    	Trail temp_fineTrail = findFirstTrailsByFilter(bson, "testTrail_fine");
	    	objFineTrail = calculations.divideTrace(temp_fineTrail, 480*60*1000);
			ArrayList<Object> result_topTrails_indexes = calculations.findTopk(objFineTrail, 1);
			objFineTrail = (ArrayList<Trail>)result_topTrails_indexes.get(0);
			ArrayList<Integer> objTopIndexs = (ArrayList<Integer>)result_topTrails_indexes.get(1);
			System.out.println("测试细粒度轨迹解析成功444");
			
			//开始判断相似度
			startTime = System.currentTimeMillis();
			double min = Integer.MAX_VALUE;
			double max = 0.0;
			for(int i = 0; i < trails.size(); i ++) {
				ArrayList<Trail> cmpFineTrail = calculations.divideTrace(trails.get(i), 480*60*1000);
				cmpFineTrail = calculations.getTopk(cmpFineTrail, objTopIndexs);
				double temp = calculations.innerSimilarity(objFineTrail, cmpFineTrail);
				trails.get(i).setScore(temp);
				min = Math.min(min, temp);
				max = Math.max(max, temp);
			}
			endTime = System.currentTimeMillis();
	    	System.out.println("训练细粒度成功555:" + (endTime - startTime) + "ms");//162528ms
			
			//评分并排序
			for(int i = 0; i < trails.size(); i ++) {
				trails.get(i).setScore((1 - (trails.get(i).getScore() - min) / (max - min)));
			}
			trails.sort(new Comparator<Trail>() {
				 @Override
				 public int compare(Trail t1,Trail t2) {
					 if(t1.getScore() < t2.getScore())
						 return 1;
					 else if(t1.getScore() == t2.getScore())
						 return 0;
					 else
						 return -1;
				 }
			});
			System.out.println("排序完成666");
			
			
			//挑选细粒度轨迹对应的原轨迹
			int count = 0;
			finTrails.add(testTrail);
			for(int i = 0; count < 9 && i < trails.size(); i ++) {
				count ++;
				bson = Filters.eq("_id", trails.get(i).getTrail_id());
				Trail trail = findFirstTrailsByFilter(bson, "trail");
				trail.setScore(trails.get(i).getScore());
				finTrails.add(trail);
			}
			String data = gson.toJson(finTrails);
            respWritter.append(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	@SuppressWarnings("unchecked")
	public static Trail findFirstTrailsByFilter(Bson bson, String collection) throws ParseException {
		ObjectId ID = null;
		ObjectId trail_ID = null;
		int Test = 0;
		String IMSI = null;
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<Long> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		Trail trail = new Trail();
    	MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", collection);
		Document document = coll.find(bson).first();
		if(document == null) {
			return trail;
		}
		ID = document.getObjectId("_id");
		trail_ID = document.getObjectId("trail_id");
		Test = (int)document.get("test");
		IMSI = (String)document.get("IMSI");
		dates = (ArrayList<Long>) document.get("tracetimes");
		longitudes = (ArrayList<Double>) document.get("longitudes");
		latitudes = (ArrayList<Double>) document.get("latitudes");
		for(int i = 0; i < dates.size(); i ++) {
			Point point = new Point();
			point.setDate(dates.get(i));
			point.setLng(longitudes.get(i));
			point.setLat(latitudes.get(i));
			//point.setCor(); 哦哦，这是原始轨迹，得等粗粒度降维的时候在计算转角，敲上瘾了-_-!
			points.add(point);
		}
		trail.setSum_points(points.size());
		trail.setPoints((ArrayList<Point>)points.clone());
		trail.setID(ID);
		trail.setTrail_id(trail_ID);
		trail.setIMSI(IMSI);
		trail.setTest(Test);
		trail.setTstart(points.get(0).getDate());
		trail.setTend(points.get(dates.size()-1).getDate());
    	return trail;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Trail> findTrailsByFilter(Bson bson, String collection) throws ParseException {
		ObjectId ID = null;
		ObjectId trail_ID = null;
		int Test = 0;
		String IMSI = null;
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<Long> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		ArrayList<Trail> trails = new ArrayList<>();
		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", collection);
		MongoCursor<Document> cursor = coll.find(bson).iterator();
		int total = 0;
    	while(total < 10000 && cursor.hasNext()) {
    		total ++;
    		Document document = cursor.next();
			ID = document.getObjectId("_id");
			trail_ID = document.getObjectId("trail_id");
			Test = (int)document.get("test");
			IMSI = (String)document.get("IMSI");
			dates = (ArrayList<Long>) document.get("tracetimes");
			longitudes = (ArrayList<Double>) document.get("longitudes");
			latitudes = (ArrayList<Double>) document.get("latitudes");
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
			trail.setTrail_id(trail_ID);
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
}
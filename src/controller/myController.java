package controller;


import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import dao.MongoUtil;
import net.sf.json.JSONArray;
import process.downloadData;
import trail.Point;
import trail.Trail;

@Controller
public class myController {
	static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/findTheTrail")
	@ResponseBody
	public void testParam(String IMSI, HttpServletResponse response) throws IOException{
	    System.out.println("IMSI: " + IMSI);
	    ArrayList<Trail> trails = new ArrayList<>();
		
	    try {
	    	//添加测试集
	    	ArrayList<Trail> testTrails = findTrails(IMSI, "testTrail");
	    	ArrayList<Trail> trainTrails = findTrails(IMSI, "trail");
	    	trails.addAll(testTrails);
	    	trails.addAll(trainTrails);
            /*将list集合装换成json对象*/
            JSONArray data = JSONArray.fromObject(trails);
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
	public ArrayList<Trail> findTrails(String IMSI, String collection) throws ParseException {
		String ID = null;
		int Test = 0;
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<String> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		ArrayList<Trail> trails = new ArrayList<>();
		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", collection);
		MongoCursor<Document> cursor = coll.find(Filters.eq("IMSI", IMSI)).iterator();
    	while(cursor.hasNext()) {
    		Document document = cursor.next();
			ID = (String)document.get("id");
			Test = (int)document.get("Test");
			dates = (ArrayList<String>) document.get("TraceTimes");
			longitudes = (ArrayList<Double>) document.get("Longitudes");
			latitudes = (ArrayList<Double>) document.get("Latitudes");
			for(int i = 0; i < dates.size(); i ++) {
				Point point = new Point();
				point.setDate(format.parse(dates.get(i)));
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
}
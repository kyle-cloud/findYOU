package controller;


import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import calculation.calculations;
import dao.MongoUtil;
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
            String data = JSON.toJSONString(trails);
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
		JSONObject.DEFFAULT_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
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
	    		String data = JSON.toJSONString(empty);
	            respWritter.append(data.toString());
	            return;
	    	}
	    	
	    	System.out.println("导入一条测试轨迹成功222");
//	    	bson = Filters.eq("Cluster_id", testTrails.get(0).getCluster_id());
//	    	ArrayList<Trail> trainTrails = findTrailsByFilter(bson, "trail");
//	    	if(trainTrails.size() == 0) {
//	    		empty.add(1);
//	    		JSONArray data = JSONArray.fromObject(empty);
//	            respWritter.append(data.toString());
//	            return;
//	    	}
//	    	
//	    	trails.addAll(testTrails);
//	    	trails.addAll(trainTrails);
	    	
	    	System.out.println("导入训练轨迹成功333");
	    	MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "testTrail_fine");
			Document document = coll.find(Filters.eq("Trail_id", testTrails.get(0).getID())).first();
			Object object = document.get("Trail");
			ArrayList<Trail> objFineTrail = new ArrayList<>();
			String jString = JSON.toJSONString(object);
			jString = "[{'hm_index':0,'score':0.0,'tend':{'date':2,'hours':21,'seconds':33,'month':1,'timezoneOffset':-480,'year':108,'minutes':50,'time':1201960233000,'day':6},'cluster_id':0,'tstart':{'date':2,'hours':15,'seconds':8,'month':1,'timezoneOffset':-480,'year':108,'minutes':36,'time':1201937768000,'day':6},'test':0,'hm':0.0,'sum_points':4,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':2,'hours':15,'seconds':8,'month':1,'timezoneOffset':-480,'year':108,'minutes':36,'time':1201937768000,'day':6},'lng':116.51172,'cor':0.0,'lat':39.92123},{'date':{'date':2,'hours':16,'seconds':8,'month':1,'timezoneOffset':-480,'year':108,'minutes':26,'time':1201940768000,'day':6},'lng':116.47179,'cor':0.0,'lat':39.90718},{'date':{'date':2,'hours':20,'seconds':33,'month':1,'timezoneOffset':-480,'year':108,'minutes':50,'time':1201956633000,'day':6},'lng':116.52231,'cor':0.0,'lat':39.91588},{'date':{'date':2,'hours':21,'seconds':33,'month':1,'timezoneOffset':-480,'year':108,'minutes':50,'time':1201960233000,'day':6},'lng':116.69167,'cor':0.0,'lat':39.85166}]},{'hm_index':1,'score':0.0,'tend':{'date':3,'hours':5,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':20,'time':1201987230000,'day':0},'cluster_id':0,'tstart':{'date':2,'hours':23,'seconds':32,'month':1,'timezoneOffset':-480,'year':108,'minutes':0,'time':1201964432000,'day':6},'test':0,'hm':0.0,'sum_points':7,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':2,'hours':23,'seconds':32,'month':1,'timezoneOffset':-480,'year':108,'minutes':0,'time':1201964432000,'day':6},'lng':116.69167,'cor':0.0,'lat':39.85175},{'date':{'date':2,'hours':23,'seconds':32,'month':1,'timezoneOffset':-480,'year':108,'minutes':50,'time':1201967432000,'day':6},'lng':116.69171,'cor':0.0,'lat':39.85182},{'date':{'date':3,'hours':0,'seconds':32,'month':1,'timezoneOffset':-480,'year':108,'minutes':50,'time':1201971032000,'day':0},'lng':116.69176,'cor':0.0,'lat':39.85165},{'date':{'date':3,'hours':2,'seconds':26,'month':1,'timezoneOffset':-480,'year':108,'minutes':21,'time':1201976486000,'day':0},'lng':116.69176,'cor':0.0,'lat':39.85183},{'date':{'date':3,'hours':3,'seconds':31,'month':1,'timezoneOffset':-480,'year':108,'minutes':30,'time':1201980631000,'day':0},'lng':116.69155,'cor':0.0,'lat':39.85181},{'date':{'date':3,'hours':4,'seconds':31,'month':1,'timezoneOffset':-480,'year':108,'minutes':20,'time':1201983631000,'day':0},'lng':116.6916,'cor':0.0,'lat':39.85156},{'date':{'date':3,'hours':5,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':20,'time':1201987230000,'day':0},'lng':116.69162,'cor':0.0,'lat':39.85157}]},{'hm_index':2,'score':0.0,'tend':{'date':3,'hours':12,'seconds':29,'month':1,'timezoneOffset':-480,'year':108,'minutes':0,'time':1202011229000,'day':0},'cluster_id':0,'tstart':{'date':3,'hours':6,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':20,'time':1201990830000,'day':0},'test':0,'hm':0.0,'sum_points':6,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':3,'hours':6,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':20,'time':1201990830000,'day':0},'lng':116.69172,'cor':0.0,'lat':39.85176},{'date':{'date':3,'hours':7,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':40,'time':1201995630000,'day':0},'lng':116.69184,'cor':0.0,'lat':39.85188},{'date':{'date':3,'hours':9,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':0,'time':1202000430000,'day':0},'lng':116.69169,'cor':0.0,'lat':39.85163},{'date':{'date':3,'hours':10,'seconds':29,'month':1,'timezoneOffset':-480,'year':108,'minutes':0,'time':1202004029000,'day':0},'lng':116.69167,'cor':0.0,'lat':39.85173},{'date':{'date':3,'hours':11,'seconds':29,'month':1,'timezoneOffset':-480,'year':108,'minutes':0,'time':1202007629000,'day':0},'lng':116.57833,'cor':0.0,'lat':39.91297},{'date':{'date':3,'hours':12,'seconds':29,'month':1,'timezoneOffset':-480,'year':108,'minutes':0,'time':1202011229000,'day':0},'lng':116.45551,'cor':0.0,'lat':39.88319}]},{'hm_index':3,'score':0.0,'tend':{'date':3,'hours':19,'seconds':14,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202037314000,'day':0},'cluster_id':0,'tstart':{'date':3,'hours':19,'seconds':14,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202037314000,'day':0},'test':0,'hm':0.0,'sum_points':1,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':3,'hours':19,'seconds':14,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202037314000,'day':0},'lng':116.69302,'cor':0.0,'lat':39.8516}]},{'hm_index':4,'score':0.0,'tend':{'date':4,'hours':2,'seconds':12,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202061912000,'day':1},'cluster_id':0,'tstart':{'date':3,'hours':20,'seconds':13,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202040913000,'day':0},'test':0,'hm':0.0,'sum_points':7,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':3,'hours':20,'seconds':13,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202040913000,'day':0},'lng':116.69155,'cor':0.0,'lat':39.85165},{'date':{'date':3,'hours':21,'seconds':13,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202044513000,'day':0},'lng':116.69159,'cor':0.0,'lat':39.85162},{'date':{'date':3,'hours':22,'seconds':13,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202048113000,'day':0},'lng':116.6916,'cor':0.0,'lat':39.85172},{'date':{'date':3,'hours':23,'seconds':12,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202051712000,'day':0},'lng':116.6916,'cor':0.0,'lat':39.85181},{'date':{'date':4,'hours':0,'seconds':13,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202054713000,'day':1},'lng':116.69161,'cor':0.0,'lat':39.85172},{'date':{'date':4,'hours':1,'seconds':13,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202058313000,'day':1},'lng':116.69162,'cor':0.0,'lat':39.8516},{'date':{'date':4,'hours':2,'seconds':12,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202061912000,'day':1},'lng':116.69162,'cor':0.0,'lat':39.85176}]},{'hm_index':5,'score':0.0,'tend':{'date':4,'hours':8,'seconds':10,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202086510000,'day':1},'cluster_id':0,'tstart':{'date':4,'hours':3,'seconds':12,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202065512000,'day':1},'test':0,'hm':0.0,'sum_points':7,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':4,'hours':3,'seconds':12,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202065512000,'day':1},'lng':116.69164,'cor':0.0,'lat':39.85169},{'date':{'date':4,'hours':4,'seconds':12,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202069112000,'day':1},'lng':116.69161,'cor':0.0,'lat':39.85169},{'date':{'date':4,'hours':5,'seconds':11,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202072711000,'day':1},'lng':116.69171,'cor':0.0,'lat':39.85171},{'date':{'date':4,'hours':6,'seconds':11,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202076311000,'day':1},'lng':116.69149,'cor':0.0,'lat':39.85166},{'date':{'date':4,'hours':6,'seconds':11,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202079311000,'day':1},'lng':116.69158,'cor':0.0,'lat':39.85168},{'date':{'date':4,'hours':7,'seconds':11,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202082911000,'day':1},'lng':116.69152,'cor':0.0,'lat':39.85141},{'date':{'date':4,'hours':8,'seconds':10,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202086510000,'day':1},'lng':116.69163,'cor':0.0,'lat':39.85156}]},{'hm_index':6,'score':0.0,'tend':{'date':4,'hours':10,'seconds':9,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202093709000,'day':1},'cluster_id':0,'tstart':{'date':4,'hours':9,'seconds':10,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202090110000,'day':1},'test':0,'hm':0.0,'sum_points':2,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':4,'hours':9,'seconds':10,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202090110000,'day':1},'lng':116.69162,'cor':0.0,'lat':39.85185},{'date':{'date':4,'hours':10,'seconds':9,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202093709000,'day':1},'lng':116.50839,'cor':0.0,'lat':39.90916}]},{'hm_index':7,'score':0.0,'tend':{'date':4,'hours':23,'seconds':37,'month':1,'timezoneOffset':-480,'year':108,'minutes':18,'time':1202138317000,'day':1},'cluster_id':0,'tstart':{'date':4,'hours':21,'seconds':39,'month':1,'timezoneOffset':-480,'year':108,'minutes':8,'time':1202130519000,'day':1},'test':0,'hm':0.0,'sum_points':3,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':4,'hours':21,'seconds':39,'month':1,'timezoneOffset':-480,'year':108,'minutes':8,'time':1202130519000,'day':1},'lng':116.69169,'cor':0.0,'lat':39.85151},{'date':{'date':4,'hours':22,'seconds':38,'month':1,'timezoneOffset':-480,'year':108,'minutes':18,'time':1202134718000,'day':1},'lng':116.69158,'cor':0.0,'lat':39.85178},{'date':{'date':4,'hours':23,'seconds':37,'month':1,'timezoneOffset':-480,'year':108,'minutes':18,'time':1202138317000,'day':1},'lng':116.69157,'cor':0.0,'lat':39.85182}]},{'hm_index':8,'score':0.0,'tend':{'date':5,'hours':6,'seconds':35,'month':1,'timezoneOffset':-480,'year':108,'minutes':8,'time':1202162915000,'day':2},'cluster_id':0,'tstart':{'date':5,'hours':0,'seconds':37,'month':1,'timezoneOffset':-480,'year':108,'minutes':28,'time':1202142517000,'day':2},'test':0,'hm':0.0,'sum_points':6,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':5,'hours':0,'seconds':37,'month':1,'timezoneOffset':-480,'year':108,'minutes':28,'time':1202142517000,'day':2},'lng':116.6916,'cor':0.0,'lat':39.85162},{'date':{'date':5,'hours':1,'seconds':36,'month':1,'timezoneOffset':-480,'year':108,'minutes':28,'time':1202146116000,'day':2},'lng':116.69165,'cor':0.0,'lat':39.85164},{'date':{'date':5,'hours':2,'seconds':36,'month':1,'timezoneOffset':-480,'year':108,'minutes':58,'time':1202151516000,'day':2},'lng':116.69182,'cor':0.0,'lat':39.85161},{'date':{'date':5,'hours':3,'seconds':36,'month':1,'timezoneOffset':-480,'year':108,'minutes':58,'time':1202155116000,'day':2},'lng':116.69155,'cor':0.0,'lat':39.85165},{'date':{'date':5,'hours':5,'seconds':36,'month':1,'timezoneOffset':-480,'year':108,'minutes':8,'time':1202159316000,'day':2},'lng':116.69153,'cor':0.0,'lat':39.85177},{'date':{'date':5,'hours':6,'seconds':35,'month':1,'timezoneOffset':-480,'year':108,'minutes':8,'time':1202162915000,'day':2},'lng':116.69162,'cor':0.0,'lat':39.8517}]},{'hm_index':9,'score':0.0,'tend':{'date':5,'hours':10,'seconds':33,'month':1,'timezoneOffset':-480,'year':108,'minutes':58,'time':1202180313000,'day':2},'cluster_id':0,'tstart':{'date':5,'hours':7,'seconds':35,'month':1,'timezoneOffset':-480,'year':108,'minutes':8,'time':1202166515000,'day':2},'test':0,'hm':0.0,'sum_points':5,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':5,'hours':7,'seconds':35,'month':1,'timezoneOffset':-480,'year':108,'minutes':8,'time':1202166515000,'day':2},'lng':116.69157,'cor':0.0,'lat':39.85171},{'date':{'date':5,'hours':7,'seconds':34,'month':1,'timezoneOffset':-480,'year':108,'minutes':58,'time':1202169514000,'day':2},'lng':116.69162,'cor':0.0,'lat':39.85176},{'date':{'date':5,'hours':8,'seconds':34,'month':1,'timezoneOffset':-480,'year':108,'minutes':58,'time':1202173114000,'day':2},'lng':116.69156,'cor':0.0,'lat':39.85172},{'date':{'date':5,'hours':9,'seconds':34,'month':1,'timezoneOffset':-480,'year':108,'minutes':58,'time':1202176714000,'day':2},'lng':116.64668,'cor':0.0,'lat':39.89091},{'date':{'date':5,'hours':10,'seconds':33,'month':1,'timezoneOffset':-480,'year':108,'minutes':58,'time':1202180313000,'day':2},'lng':116.46352,'cor':0.0,'lat':39.91971}]},{'hm_index':10,'score':0.0,'tend':{'date':5,'hours':18,'seconds':20,'month':1,'timezoneOffset':-480,'year':108,'minutes':51,'time':1202208680000,'day':2},'cluster_id':0,'tstart':{'date':5,'hours':15,'seconds':23,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202194883000,'day':2},'test':0,'hm':0.0,'sum_points':5,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':5,'hours':15,'seconds':23,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202194883000,'day':2},'lng':116.49344,'cor':0.0,'lat':39.91441},{'date':{'date':5,'hours':16,'seconds':21,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202198481000,'day':2},'lng':116.44923,'cor':0.0,'lat':39.9797},{'date':{'date':5,'hours':16,'seconds':21,'month':1,'timezoneOffset':-480,'year':108,'minutes':51,'time':1202201481000,'day':2},'lng':116.46432,'cor':0.0,'lat':39.9139},{'date':{'date':5,'hours':17,'seconds':20,'month':1,'timezoneOffset':-480,'year':108,'minutes':51,'time':1202205080000,'day':2},'lng':116.43479,'cor':0.0,'lat':39.94799},{'date':{'date':5,'hours':18,'seconds':20,'month':1,'timezoneOffset':-480,'year':108,'minutes':51,'time':1202208680000,'day':2},'lng':116.50337,'cor':0.0,'lat':39.90693}]},{'hm_index':11,'score':0.0,'tend':{'date':6,'hours':3,'seconds':40,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202238940000,'day':3},'cluster_id':0,'tstart':{'date':5,'hours':22,'seconds':41,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202223341000,'day':2},'test':0,'hm':0.0,'sum_points':5,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':5,'hours':22,'seconds':41,'month':1,'timezoneOffset':-480,'year':108,'minutes':55,'time':1202223341000,'day':2},'lng':116.69157,'cor':0.0,'lat':39.85183},{'date':{'date':5,'hours':23,'seconds':41,'month':1,'timezoneOffset':-480,'year':108,'minutes':45,'time':1202226341000,'day':2},'lng':116.69159,'cor':0.0,'lat':39.85182},{'date':{'date':6,'hours':0,'seconds':41,'month':1,'timezoneOffset':-480,'year':108,'minutes':45,'time':1202229941000,'day':3},'lng':116.69142,'cor':0.0,'lat':39.85161},{'date':{'date':6,'hours':2,'seconds':40,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202234740000,'day':3},'lng':116.69173,'cor':0.0,'lat':39.85173},{'date':{'date':6,'hours':3,'seconds':40,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202238940000,'day':3},'lng':116.69163,'cor':0.0,'lat':39.85169}]},{'hm_index':12,'score':0.0,'tend':{'date':6,'hours':10,'seconds':38,'month':1,'timezoneOffset':-480,'year':108,'minutes':25,'time':1202264738000,'day':3},'cluster_id':0,'tstart':{'date':6,'hours':4,'seconds':40,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202242540000,'day':3},'test':0,'hm':0.0,'sum_points':7,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':6,'hours':4,'seconds':40,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202242540000,'day':3},'lng':116.69156,'cor':0.0,'lat':39.85165},{'date':{'date':6,'hours':5,'seconds':39,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202245539000,'day':3},'lng':116.69162,'cor':0.0,'lat':39.85175},{'date':{'date':6,'hours':6,'seconds':39,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202249139000,'day':3},'lng':116.6915,'cor':0.0,'lat':39.85164},{'date':{'date':6,'hours':7,'seconds':39,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202253339000,'day':3},'lng':116.69156,'cor':0.0,'lat':39.85169},{'date':{'date':6,'hours':8,'seconds':39,'month':1,'timezoneOffset':-480,'year':108,'minutes':15,'time':1202256939000,'day':3},'lng':116.69159,'cor':0.0,'lat':39.85183},{'date':{'date':6,'hours':9,'seconds':39,'month':1,'timezoneOffset':-480,'year':108,'minutes':25,'time':1202261139000,'day':3},'lng':116.69159,'cor':0.0,'lat':39.8516},{'date':{'date':6,'hours':10,'seconds':38,'month':1,'timezoneOffset':-480,'year':108,'minutes':25,'time':1202264738000,'day':3},'lng':116.5727,'cor':0.0,'lat':39.91361}]},{'hm_index':13,'score':0.0,'tend':{'date':6,'hours':15,'seconds':43,'month':1,'timezoneOffset':-480,'year':108,'minutes':37,'time':1202283463000,'day':3},'cluster_id':0,'tstart':{'date':6,'hours':14,'seconds':43,'month':1,'timezoneOffset':-480,'year':108,'minutes':37,'time':1202279863000,'day':3},'test':0,'hm':0.0,'sum_points':2,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':6,'hours':14,'seconds':43,'month':1,'timezoneOffset':-480,'year':108,'minutes':37,'time':1202279863000,'day':3},'lng':116.42589,'cor':0.0,'lat':39.85721},{'date':{'date':6,'hours':15,'seconds':43,'month':1,'timezoneOffset':-480,'year':108,'minutes':37,'time':1202283463000,'day':3},'lng':116.48335,'cor':0.0,'lat':39.89111}]},{'hm_index':14,'score':0.0,'tend':{'date':6,'hours':23,'seconds':31,'month':1,'timezoneOffset':-480,'year':108,'minutes':41,'time':1202312491000,'day':3},'cluster_id':0,'tstart':{'date':6,'hours':19,'seconds':21,'month':1,'timezoneOffset':-480,'year':108,'minutes':41,'time':1202298081000,'day':3},'test':0,'hm':0.0,'sum_points':2,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':6,'hours':19,'seconds':21,'month':1,'timezoneOffset':-480,'year':108,'minutes':41,'time':1202298081000,'day':3},'lng':116.50382,'cor':0.0,'lat':39.90692},{'date':{'date':6,'hours':23,'seconds':31,'month':1,'timezoneOffset':-480,'year':108,'minutes':41,'time':1202312491000,'day':3},'lng':116.69173,'cor':0.0,'lat':39.8515}]},{'hm_index':15,'score':0.0,'tend':{'date':7,'hours':7,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202338890000,'day':4},'cluster_id':0,'tstart':{'date':7,'hours':0,'seconds':32,'month':1,'timezoneOffset':-480,'year':108,'minutes':41,'time':1202316092000,'day':4},'test':0,'hm':0.0,'sum_points':7,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':7,'hours':0,'seconds':32,'month':1,'timezoneOffset':-480,'year':108,'minutes':41,'time':1202316092000,'day':4},'lng':116.69185,'cor':0.0,'lat':39.85205},{'date':{'date':7,'hours':1,'seconds':31,'month':1,'timezoneOffset':-480,'year':108,'minutes':41,'time':1202319691000,'day':4},'lng':116.69174,'cor':0.0,'lat':39.85151},{'date':{'date':7,'hours':2,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':51,'time':1202323890000,'day':4},'lng':116.69157,'cor':0.0,'lat':39.85156},{'date':{'date':7,'hours':3,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':51,'time':1202327490000,'day':4},'lng':116.69175,'cor':0.0,'lat':39.85165},{'date':{'date':7,'hours':4,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':51,'time':1202331090000,'day':4},'lng':116.69178,'cor':0.0,'lat':39.85217},{'date':{'date':7,'hours':6,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202335290000,'day':4},'lng':116.69172,'cor':0.0,'lat':39.85148},{'date':{'date':7,'hours':7,'seconds':30,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202338890000,'day':4},'lng':116.69173,'cor':0.0,'lat':39.85172}]},{'hm_index':16,'score':0.0,'tend':{'date':7,'hours':11,'seconds':35,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202353535000,'day':4},'cluster_id':0,'tstart':{'date':7,'hours':8,'seconds':29,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202342489000,'day':4},'test':0,'hm':0.0,'sum_points':4,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':7,'hours':8,'seconds':29,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202342489000,'day':4},'lng':116.69164,'cor':0.0,'lat':39.85156},{'date':{'date':7,'hours':9,'seconds':28,'month':1,'timezoneOffset':-480,'year':108,'minutes':11,'time':1202346688000,'day':4},'lng':116.69172,'cor':0.0,'lat':39.85156},{'date':{'date':7,'hours':10,'seconds':28,'month':1,'timezoneOffset':-480,'year':108,'minutes':11,'time':1202350288000,'day':4},'lng':116.69164,'cor':0.0,'lat':39.85153},{'date':{'date':7,'hours':11,'seconds':35,'month':1,'timezoneOffset':-480,'year':108,'minutes':5,'time':1202353535000,'day':4},'lng':116.57738,'cor':0.0,'lat':39.91294}]},{'hm_index':17,'score':0.0,'tend':{'date':7,'hours':21,'seconds':52,'month':1,'timezoneOffset':-480,'year':108,'minutes':24,'time':1202390692000,'day':4},'cluster_id':0,'tstart':{'date':7,'hours':18,'seconds':52,'month':1,'timezoneOffset':-480,'year':108,'minutes':34,'time':1202380492000,'day':4},'test':0,'hm':0.0,'sum_points':4,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':7,'hours':18,'seconds':52,'month':1,'timezoneOffset':-480,'year':108,'minutes':34,'time':1202380492000,'day':4},'lng':116.51731,'cor':0.0,'lat':39.91134},{'date':{'date':7,'hours':19,'seconds':52,'month':1,'timezoneOffset':-480,'year':108,'minutes':34,'time':1202384092000,'day':4},'lng':116.69181,'cor':0.0,'lat':39.85168},{'date':{'date':7,'hours':20,'seconds':52,'month':1,'timezoneOffset':-480,'year':108,'minutes':24,'time':1202387092000,'day':4},'lng':116.69158,'cor':0.0,'lat':39.85189},{'date':{'date':7,'hours':21,'seconds':52,'month':1,'timezoneOffset':-480,'year':108,'minutes':24,'time':1202390692000,'day':4},'lng':116.69166,'cor':0.0,'lat':39.85154}]},{'hm_index':18,'score':0.0,'tend':{'date':8,'hours':3,'seconds':50,'month':1,'timezoneOffset':-480,'year':108,'minutes':54,'time':1202414090000,'day':5},'cluster_id':0,'tstart':{'date':7,'hours':22,'seconds':52,'month':1,'timezoneOffset':-480,'year':108,'minutes':34,'time':1202394892000,'day':4},'test':0,'hm':0.0,'sum_points':6,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':7,'hours':22,'seconds':52,'month':1,'timezoneOffset':-480,'year':108,'minutes':34,'time':1202394892000,'day':4},'lng':116.69136,'cor':0.0,'lat':39.85164},{'date':{'date':7,'hours':23,'seconds':52,'month':1,'timezoneOffset':-480,'year':108,'minutes':34,'time':1202398492000,'day':4},'lng':116.6916,'cor':0.0,'lat':39.85154},{'date':{'date':8,'hours':0,'seconds':51,'month':1,'timezoneOffset':-480,'year':108,'minutes':24,'time':1202401491000,'day':5},'lng':116.69152,'cor':0.0,'lat':39.85167},{'date':{'date':8,'hours':1,'seconds':51,'month':1,'timezoneOffset':-480,'year':108,'minutes':24,'time':1202405091000,'day':5},'lng':116.69151,'cor':0.0,'lat':39.85161},{'date':{'date':8,'hours':2,'seconds':51,'month':1,'timezoneOffset':-480,'year':108,'minutes':54,'time':1202410491000,'day':5},'lng':116.69158,'cor':0.0,'lat':39.8516},{'date':{'date':8,'hours':3,'seconds':50,'month':1,'timezoneOffset':-480,'year':108,'minutes':54,'time':1202414090000,'day':5},'lng':116.69153,'cor':0.0,'lat':39.85165}]},{'hm_index':19,'score':0.0,'tend':{'date':8,'hours':10,'seconds':48,'month':1,'timezoneOffset':-480,'year':108,'minutes':44,'time':1202438688000,'day':5},'cluster_id':0,'tstart':{'date':8,'hours':4,'seconds':49,'month':1,'timezoneOffset':-480,'year':108,'minutes':54,'time':1202417689000,'day':5},'test':0,'hm':0.0,'sum_points':7,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':8,'hours':4,'seconds':49,'month':1,'timezoneOffset':-480,'year':108,'minutes':54,'time':1202417689000,'day':5},'lng':116.69169,'cor':0.0,'lat':39.85076},{'date':{'date':8,'hours':5,'seconds':49,'month':1,'timezoneOffset':-480,'year':108,'minutes':54,'time':1202421289000,'day':5},'lng':116.69163,'cor':0.0,'lat':39.85178},{'date':{'date':8,'hours':6,'seconds':49,'month':1,'timezoneOffset':-480,'year':108,'minutes':54,'time':1202424889000,'day':5},'lng':116.69152,'cor':0.0,'lat':39.85164},{'date':{'date':8,'hours':7,'seconds':49,'month':1,'timezoneOffset':-480,'year':108,'minutes':44,'time':1202427889000,'day':5},'lng':116.69158,'cor':0.0,'lat':39.85165},{'date':{'date':8,'hours':8,'seconds':49,'month':1,'timezoneOffset':-480,'year':108,'minutes':44,'time':1202431489000,'day':5},'lng':116.69162,'cor':0.0,'lat':39.85161},{'date':{'date':8,'hours':9,'seconds':49,'month':1,'timezoneOffset':-480,'year':108,'minutes':44,'time':1202435089000,'day':5},'lng':116.69162,'cor':0.0,'lat':39.85147},{'date':{'date':8,'hours':10,'seconds':48,'month':1,'timezoneOffset':-480,'year':108,'minutes':44,'time':1202438688000,'day':5},'lng':116.60394,'cor':0.0,'lat':39.90741}]},{'hm_index':20,'score':0.0,'tend':{'date':8,'hours':15,'seconds':31,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202454091000,'day':5},'cluster_id':0,'tstart':{'date':8,'hours':15,'seconds':31,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202454091000,'day':5},'test':0,'hm':0.0,'sum_points':1,'IMSI':'1','ID':{'date':{'date':31,'hours':17,'seconds':3,'month':2,'timezoneOffset':-480,'year':120,'minutes':5,'time':1585645503000,'day':2},'machineIdentifier':11739625,'counter':13893185,'processIdentifier':5424,'time':1585645503000,'timeSecond':1585645503,'timestamp':1585645503},'points':[{'date':{'date':8,'hours':15,'seconds':31,'month':1,'timezoneOffset':-480,'year':108,'minutes':1,'time':1202454091000,'day':5},'lng':116.44152,'cor':0.0,'lat':39.93236}]}]";
			objFineTrail =  JSON.parseObject(jString, new TypeReference<ArrayList<Trail>>(){});
			
			ArrayList<Object> result_topTrails_indexes = calculations.findTopk(objFineTrail, 1);
			objFineTrail = (ArrayList<Trail>)result_topTrails_indexes.get(0);
			ArrayList<Integer> objTopIndexs = (ArrayList<Integer>)result_topTrails_indexes.get(1);
			
			System.out.println("测试细粒度轨迹解析成功444");
			coll = MongoUtil.instance.getCollection("liu", "trail_fine");
			double min = Integer.MAX_VALUE;
			double max = 0.0;
//			for(int i = testTrails.size(); i < trails.size(); i ++) {
//				document = coll.find(Filters.eq("Trail_id", trails.get(i).getID())).first();
//				object = document.get("Trail");
//				ArrayList<Trail> cmpFineTrail = new ArrayList<>();
//				jArray = JSONArray.fromObject(object);
//				for(int j = 0; j < jArray.size(); j ++) {
//					JSONObject jsonObject = jArray.getJSONObject(j);
//					cmpFineTrail.add(gson.fromJson(gson.toJson(jsonObject), Trail.class));
//				}
//				cmpFineTrail = calculations.getTopk(cmpFineTrail, objTopIndexs);
//				double temp = calculations.innerSimilarity(objFineTrail, cmpFineTrail);
//				trails.get(i).setScore(temp);
//				min = Math.min(min, temp);
//				max = Math.max(max, temp);
//			}
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
			String data = JSON.toJSONString(finTrails);
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
		ArrayList<String> dates = new ArrayList<>();
		ArrayList<Double> longitudes = new ArrayList<>();
		ArrayList<Double> latitudes = new ArrayList<>();
		ArrayList<Trail> trails = new ArrayList<>();
    	MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", collection);
		Document document = coll.find(bson).first();
		ID = document.getObjectId("_id");
		Test = (int)document.get("Test");
		IMSI = (String)document.get("IMSI");
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
    	return trails;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Trail> findTrailsByFilter(Bson bson, String collection) throws ParseException {
		ObjectId ID = null;
		int Test = 0;
		String IMSI = null;
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<String> dates = new ArrayList<>();
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
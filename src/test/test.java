package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.mail.Flags.Flag;

import org.bson.Document;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;

import com.google.gson.Gson;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mongodb.annotations.Beta;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.sun.javafx.geom.PickRay;
import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;
import com.sun.org.apache.bcel.internal.generic.StackConsumer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import calculation.calculations;
import dao.MongoUtil;
import javafx.css.PseudoClass;
import process.downloadData;
import sun.tools.jar.resources.jar;
import trail.Point;
import trail.Trail;

public class test {
	public static void testTimeSegment() {
		ArrayList<Trail> trails_tmp = downloadData.getTrails("trail");
		ArrayList<Trail> trails = new ArrayList<>();
		for(int i = 0; i < 100; i ++) {
			int rd = (int) (Math.random() * 180000);
			trails.add(trails_tmp.get(rd));
		}
		HashMap<Double, Integer> map = new HashMap<Double, Integer>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 480*60*1000);
			int sum = 0;
			for(int j = 0; j < dividedTrail.size(); j ++) {
				if(dividedTrail.get(j).getSum_points() >= 5) {
					sum ++;
				}
			}
			double percent = (double)sum / (double)dividedTrail.size();
			double key = (double)Math.round(percent * 10) / 10;
			if(map.containsKey(key)) {
				map.put(key, map.get(key) + 1);
			} else {
				map.put(key, 1);
			}
		}
		map.forEach((k, v) ->
			System.out.println(k + ":" + v)
		);
	}
	
	public ArrayList<Object> testCompressOnMap() throws Exception {
		ArrayList<Object> result = new ArrayList<>();
		ArrayList<Trail> trails = downloadData.getTrails("trail");
		ArrayList<Trail> coarseTrails = new ArrayList<>();
		ArrayList<ArrayList<Trail>> fineTrails = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 480*60*1000);
			ArrayList<Point> coarseTrail = calculations.coarseCompress(dividedTrail);
			Trail coarse_Trail = new Trail();
			coarse_Trail.setIMSI(trails.get(i).getIMSI());
			coarse_Trail.setPoints(coarseTrail);
			coarse_Trail.setSum_points(coarseTrail.size());
			coarse_Trail.setTstart(coarseTrail.get(0).getDate());
			coarse_Trail.setTend(coarseTrail.get(coarseTrail.size()-1).getDate());
			coarseTrails.add(coarse_Trail);
			
			ArrayList<Trail> fineTrail = calculations.fineCompress(dividedTrail, 5000, (long)120*60*1000);
			fineTrails.add(fineTrail);
		}
		result.add(trails);
		result.add(coarseTrails);
		result.add(fineTrails);
		return result;
	}
	
	public static void testCompressOnNumber() throws Exception {
		ArrayList<Trail> trails_tmp = downloadData.getTrails("trail");
		ArrayList<Trail> trails = new ArrayList<>();
		for(int i = 0; i < 100; i ++) {
			int rd = (int) (Math.random() * 180000);
			trails.add(trails_tmp.get(rd));
		}
		ArrayList<ArrayList<Point>> evenTrails = new ArrayList<>();
		ArrayList<ArrayList<Point>> coarseTrails = new ArrayList<>();
		ArrayList<ArrayList<Trail>> fineTrails = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Point> temp_points = new ArrayList<>();
			for(int j = 0; j < trails.get(i).getPoints().size(); j += 3) {
				temp_points.add(trails.get(i).getPoints().get(j));
			}
			evenTrails.add(temp_points);
			
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 480*60*1000);
			
			ArrayList<Point> coarseTrail = calculations.coarseCompress(dividedTrail);
			coarseTrails.add(coarseTrail);
			
			ArrayList<Trail> fineTrail = calculations.fineCompress(dividedTrail, 5000, (long)120*60*1000);
			fineTrails.add(fineTrail);
		}
		File f = new File("number_trails.txt");
		if(!f.exists()) f.createNewFile();
		FileWriter fWriter = new FileWriter(f, true);
		BufferedWriter bWriter = new BufferedWriter(fWriter);
		for(int i = 0; i < trails.size(); i ++) {
			bWriter.write("" + i + ":" + trails.get(i).getPoints().size() + "\n");
		}
		bWriter.close();
		
		File f1 = new File("number_coarseTrails.txt");
		if(!f1.exists()) f1.createNewFile();
		fWriter = new FileWriter(f1, true);
		bWriter = new BufferedWriter(fWriter);
		for(int i = 0; i < coarseTrails.size(); i ++) {
			bWriter.write("" + i + ":" + coarseTrails.get(i).size() + "\n");
		}
		bWriter.close();
		
		File f2 = new File("number_fineTrails.txt");
		if(!f2.exists()) f2.createNewFile();
		fWriter = new FileWriter(f2, true);
		bWriter = new BufferedWriter(fWriter);
		for(int i = 0; i < fineTrails.size(); i ++) {
			int sum = 0;
			for(int j = 0; j < fineTrails.get(i).size(); j ++) {
				sum += fineTrails.get(i).get(j).getPoints().size();
			}
			bWriter.write("" + i + ":" + sum + "\n");
		}
		bWriter.close();
		
		File f3 = new File("number_evenTrails.txt");
		if(!f3.exists()) f3.createNewFile();
		fWriter = new FileWriter(f3, true);
		bWriter = new BufferedWriter(fWriter);
		for(int i = 0; i < evenTrails.size(); i ++) {
			bWriter.write("" + i + ":" + evenTrails.get(i).size() + "\n");
		}
		bWriter.close();
	}
	
	@SuppressWarnings("unchecked")
	public static void testCompressOnHausdorff() throws Exception {
		ArrayList<Trail> trails_tmp = downloadData.getTrails("trail");
		ArrayList<Trail> trails = new ArrayList<>();
		for(int i = 0; i < 101; i ++) {
			int rd = (int) (Math.random() * 180000);
			trails.add(trails_tmp.get(rd));
		}
		ArrayList<ArrayList<Point>> evenTrails = new ArrayList<>();
		ArrayList<ArrayList<Point>> coarseTrails = new ArrayList<>();
		ArrayList<ArrayList<Trail>> fineTrails = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Point> temp_points = new ArrayList<>();
			for(int j = 0; j < trails.get(i).getPoints().size(); j += 3) {
				temp_points.add(trails.get(i).getPoints().get(j));
			}
			evenTrails.add(temp_points);
			
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 480*60*1000);
			
			ArrayList<Point> coarseTrail = calculations.coarseCompress(dividedTrail);
			coarseTrails.add(coarseTrail);
			
			ArrayList<Trail> fineTrail = calculations.fineCompress(dividedTrail, 5000, (long)120*60*1000);
			fineTrails.add(fineTrail);
		}
		File f = new File("hausdorff_trails.txt");
		if(!f.exists()) f.createNewFile();
		FileWriter fWriter = new FileWriter(f, true);
		BufferedWriter bWriter = new BufferedWriter(fWriter);
		for(int i = 1; i < trails.size(); i ++) {
			bWriter.write("" + i + ":" + calculations.calcHk(trails.get(0).getPoints(), trails.get(i).getPoints()) + "\n");
		}
		bWriter.close();
		
		File f1 = new File("hausdorff_coarseTrails.txt");
		if(!f1.exists()) f1.createNewFile();
		fWriter = new FileWriter(f1, true);
		bWriter = new BufferedWriter(fWriter);
		for(int i = 1; i < coarseTrails.size(); i ++) {
			bWriter.write("" + i + ":" + calculations.calcHk(coarseTrails.get(0), coarseTrails.get(i)) + "\n");
		}
		bWriter.close();
		
		File f2 = new File("hausdorff_fineTrails.txt");
		if(!f2.exists()) f2.createNewFile();
		fWriter = new FileWriter(f2, true);
		bWriter = new BufferedWriter(fWriter);
		ArrayList<Point> fixed_points = new ArrayList<>();
		for(int i = 0; i < fineTrails.size(); i ++) {
			ArrayList<Trail> trail = fineTrails.get(i);
			ArrayList<Point> points = new ArrayList<>();
			for(int j = 0; j < trail.size(); j ++) {
				points.addAll(trail.get(j).getPoints());
			}
			if(i == 0) {
				fixed_points = (ArrayList<Point>) points.clone();
				continue;
			}
			bWriter.write("" + i + ":" + calculations.calcHk(fixed_points, points) + "\n");
		}
		bWriter.close();
		
		File f3 = new File("hausdorff_evenTrails.txt");
		if(!f3.exists()) f3.createNewFile();
		fWriter = new FileWriter(f3, true);
		bWriter = new BufferedWriter(fWriter);
		for(int i = 1; i < evenTrails.size(); i ++) {
			bWriter.write("" + i + ":" + calculations.calcHk(evenTrails.get(0), evenTrails.get(i)) + "\n");
		}
		bWriter.close();
	}
	
	@SuppressWarnings("unchecked")
	public static void testBelta() throws Exception {
		File f = new File("topk_belta.txt");
		if(!f.exists()) f.createNewFile();
		FileWriter fWriter = new FileWriter(f, true);
		BufferedWriter bWriter = new BufferedWriter(fWriter);

		ArrayList<Trail> trails_tmp = downloadData.getTrails("trail");
		ArrayList<Trail> trails = new ArrayList<>();
		for(int i = 0; i < 100; i ++) {
			int rd = (int) (Math.random() * 180000);
			trails.add(trails_tmp.get(rd));
		}
		for(int i = 0; i < 10; i ++) {
			double belta = 1.1;
			for(int k = 0; k < 10; k ++) {
				belta -= 0.1;
				int obj = (int)(Math.random() * trails.size());
				ArrayList<Trail> objTrail = calculations.divideTrace(trails.get(obj), 420*60*1000);
				ArrayList<Object> result_topTrails_indexes = calculations.findTopk(objTrail, belta);
				ArrayList<Integer> objTopIndexs = (ArrayList<Integer>)result_topTrails_indexes.get(1); //找到目标轨迹提取的片段下标
				objTrail = (ArrayList<Trail>)result_topTrails_indexes.get(0);
				
				double average = 0.0;
				for(int j = 0; j < trails.size(); j ++) {
					//System.out.println(j);
					ArrayList<Trail> cmpTrail = calculations.divideTrace(trails.get(j), 420*60*1000);
					cmpTrail = calculations.getTopk(cmpTrail, objTopIndexs);
					average += calculations.calcH(objTrail, cmpTrail);
				}
				average /= (double)trails.size();
				bWriter.write((double) Math.round(belta*10)/10 + ":" +  average + "  ");
			}
			bWriter.write("\n");
		}
		bWriter.close();
	}
	
	public static void testCluster() throws Exception {
		ArrayList<Trail> testTrails = downloadData.getTrails("testTrail");
		ArrayList<Trail> trainTrails = downloadData.getTrails("trail");
		ArrayList<Trail> trails = new ArrayList<>();
		trails.addAll(testTrails);
		trails.addAll(trainTrails);
		ArrayList<Trail> finTrails = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 480*60*1000);
			ArrayList<Point> coarseTrail = calculations.coarseCompress(dividedTrail);
			Trail coarse_finTrail = new Trail();
			coarse_finTrail.setIMSI(trails.get(i).getIMSI());
			coarse_finTrail.setPoints(coarseTrail);
			coarse_finTrail.setSum_points(coarseTrail.size());
			coarse_finTrail.setTstart(coarseTrail.get(0).getDate());
			coarse_finTrail.setTend(coarseTrail.get(coarseTrail.size()-1).getDate());
			finTrails.add(coarse_finTrail);
			System.out.println(i);
		}

		System.out.println(calculations.structCluster(finTrails, 0.8, 0.80, 100).size()); //, finTrails.get(0)
		//看一下每条轨迹分到的集合
		for(int i = 0; i < finTrails.size(); i ++) {
			System.out.println(i + " : " + finTrails.get(i).getCluster_id());
		}
		
		//计算聚类后最小和最大的轨迹集合
		finTrails.sort(new Comparator<Trail>() {
			 @Override
			 public int compare(Trail t1, Trail t2) {
				 if(t1.getCluster_id() < t2.getCluster_id())
					 return -1;
				 else if(t1.getCluster_id() == t2.getCluster_id())
					 return 0;
				 else
					 return 1;
			 }
		});
		int count = 1;		
 		int min = 20000;		
 		int max = -1;		
 		int flag = 0;
 		for(int i = 0; i < finTrails.size(); i ++) {
 			//System.out.println(finTrails.get(i).getCluster_id());
 			if(finTrails.get(i).getCluster_id() == 0) continue;
 			if(flag == 0) {
 				i ++;
 				flag = 1;
 			}
 			if(finTrails.get(i - 1).getCluster_id() == finTrails.get(i).getCluster_id()) count ++;		
 			else {		
 				min = Math.min(min, count);		
 				max = Math.max(max, count);		
 				count = 1;
 			}
 		}
 		min = Math.min(min, count);
 		max = Math.max(max, count);
 		System.out.println(min + "-" + max);
 		
 		System.out.println(finTrails.get(finTrails.size() - 1).getCluster_id());
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *找到最相似的十条轨迹
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void testFindTopTrails() throws Exception {
		ArrayList<Trail> trails = downloadData.getTrails("trail");
		ArrayList<Trail> finTrails = new ArrayList<>();
		ArrayList<Double> hausdorff_final = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 240*60*1000);
			ArrayList<Point> coarseTrail = calculations.coarseCompress(dividedTrail);
			Trail coarse_finTrail = new Trail();
			coarse_finTrail.setIMSI(trails.get(i).getIMSI());
			coarse_finTrail.setPoints(coarseTrail);
			coarse_finTrail.setSum_points(coarseTrail.size());
			coarse_finTrail.setTstart(coarseTrail.get(0).getDate());
			coarse_finTrail.setTend(coarseTrail.get(coarseTrail.size()-1).getDate());
			finTrails.add(coarse_finTrail);
		}
		ArrayList<Trail> cluseredTrails = calculations.structCluster(finTrails, 0.8, 0.80, 50);//, finTrails.get(0)////
		
		ArrayList<Trail> objTrail = calculations.divideTrace(trails.get(0), 420*60*1000);
		ArrayList<Trail> objFineTrail = calculations.fineCompress(objTrail, 3000, (long)30*60*1000);
		ArrayList<Object> result_topTrails_indexes = calculations.findTopk(objFineTrail, 1);
		objFineTrail = (ArrayList<Trail>)result_topTrails_indexes.get(0);
		
		double min = Integer.MAX_VALUE;
		double max = 0.0;
		for(int i = 0; i < finTrails.size(); i ++) {
			ArrayList<Trail> cmpTrail = calculations.divideTrace(cluseredTrails.get(i), 420*60*1000);
			ArrayList<Trail> cmpFineTrail = calculations.fineCompress(cmpTrail, 3000, (long)30*60*1000);
			ArrayList<Integer> objTopIndexs = (ArrayList<Integer>)result_topTrails_indexes.get(1);
			cmpFineTrail = calculations.getTopk(cmpFineTrail, objTopIndexs);
			double temp = calculations.innerSimilarity(objFineTrail, cmpFineTrail);
			hausdorff_final.add(temp);
			min = Math.min(min, temp);
			max = Math.max(max, temp);
		}
		for(int i = 0; i < finTrails.size(); i ++) {
			System.out.println(1 - (hausdorff_final.get(i) - min) / (max - min));
		}
	}
	
	public static void testMongoDB() {
		//提取对象形式存储的细粒度轨迹
		long startTime = System.currentTimeMillis();
		Gson gson = new Gson();
		ArrayList<Trail> fineTrails = new ArrayList<>();
		MongoCollection<Document> coll = MongoUtil.instance.getCollection("liu", "trail_fine");
		MongoCursor<Document> cursor = coll.find().iterator();
    	while(cursor.hasNext()) {
    		Document document = cursor.next();
			String jString = gson.toJson(document).toString();
			fineTrails.add(gson.fromJson(jString, Trail.class));
    	}
    	long endTime = System.currentTimeMillis();
    	System.out.println("细粒度运行时间：" + (endTime - startTime) + "ms"); //113.567
    	
    	//提取原始轨迹并直接整合成轨迹
    	startTime = System.currentTimeMillis();
    	downloadData.getTrails("trail");
    	endTime = System.currentTimeMillis();
    	System.out.println("原始运行时间：" + (endTime - startTime) + "ms"); //11.32
	}
	
	public static void testTimeOnHarsdorff() throws MWException {
		ArrayList<Trail> trails = downloadData.getTrails("trail");
		for(int i = 0; i < trails.size(); i ++) {
			long startTime = System.currentTimeMillis();
			calculations.calcHk(trails.get(0).getPoints(), trails.get(i).getPoints());
			System.out.println(i);
			long endTime = System.currentTimeMillis();
			System.out.println("原始运行时间：" + (endTime - startTime) + "ms"); //11.32
		}
	}
	
	public static void testFineCompressAdvanced() throws IOException {
		ArrayList<Trail> trails = downloadData.getTrails("trail");
		ArrayList<ArrayList<Trail>> fineTrails = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 480*60*1000);
			ArrayList<Trail> fineTrail = calculations.fineCompress(dividedTrail, 5000, (long)120*60*1000);
			fineTrails.add(fineTrail);
		}
		
		File f2 = new File("number_fineTrails.txt");
		if(!f2.exists()) f2.createNewFile();
		FileWriter fWriter = new FileWriter(f2, true);
		BufferedWriter bWriter = new BufferedWriter(fWriter);
		bWriter.write('\n');
		for(int i = 0; i < fineTrails.size(); i ++) {
			ArrayList<Point> points = new ArrayList<>();
			for(int j = 0; j < fineTrails.get(i).size(); j ++) {
				points.addAll(fineTrails.get(i).get(j).getPoints());
			}
			bWriter.write("" + i + ":" + calculations.calcHk(points, trails.get(i).getPoints()) + "\t");
		}
		bWriter.close();
	}
	
	public static void main(String[] args) throws Exception {
		//testTimeSegment();
		//testCompressOnNumber();
		//testCompressOnHausdorff(); // 最后是要计算与（原始轨迹-原始轨迹-距离）的结果进行比较（差值）
		//testBelta();
		testCluster();
		//testFindTopTrails();
		//testMongoDB();
		//testTimeOnHarsdorff();
		//testFineCompressAdvanced();
		//副本集设置成功，牛批
	}
}

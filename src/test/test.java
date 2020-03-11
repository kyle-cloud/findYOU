package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import calculation.calculations;
import process.downloadData;
import trail.Point;
import trail.Trail;

public class test {
	public static void testTimeSegment() {
		HashMap<Double, Integer> map = new HashMap<Double, Integer>();
		ArrayList<Trail> trails = downloadData.getTrails("trail");
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 240*60*1000);
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
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 240*60*1000);
			ArrayList<Point> coarseTrail = calculations.coarseCompress(dividedTrail);
			Trail coarse_Trail = new Trail();
			coarse_Trail.setIMSI(trails.get(i).getIMSI());
			coarse_Trail.setPoints(coarseTrail);
			coarse_Trail.setSum_points(coarseTrail.size());
			coarse_Trail.setTstart(coarseTrail.get(0).getDate());
			coarse_Trail.setTend(coarseTrail.get(coarseTrail.size()-1).getDate());
			coarseTrails.add(coarse_Trail);
			
			ArrayList<Trail> fineTrail = calculations.fineCompress(dividedTrail, 3000, (long)30*60*1000);
			fineTrails.add(fineTrail);
		}
		result.add(trails);
		result.add(coarseTrails);
		result.add(fineTrails);
		return result;
	}
	
	public static void testCompress() throws Exception {
		ArrayList<Trail> trails = downloadData.getTrails("trail");
		ArrayList<Trail> coarseTrails = new ArrayList<>();
		ArrayList<ArrayList<Trail>> fineTrails = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 240*60*1000);
			
			ArrayList<Point> coarseTrail = calculations.coarseCompress(dividedTrail);
			Trail coarse_Trail = new Trail();
			coarse_Trail.setPoints(coarseTrail);
			coarseTrails.add(coarse_Trail);
			
			ArrayList<Trail> fineTrail = calculations.fineCompress(dividedTrail, 3000, (long)30*60*1000);
			fineTrails.add(fineTrail);
		}
		File f = new File("trails.txt");
		if(!f.exists()) f.createNewFile();
		FileWriter fWriter = new FileWriter(f, true);
		BufferedWriter bWriter = new BufferedWriter(fWriter);
		for(int i = 0; i < trails.size(); i ++) {
			bWriter.write("" + i + ":" + trails.get(i).getPoints().size() + "\n");
		}
		bWriter.close();
		
		File f1 = new File("coarseTrails.txt");
		if(!f1.exists()) f1.createNewFile();
		fWriter = new FileWriter(f1, true);
		bWriter = new BufferedWriter(fWriter);
		for(int i = 0; i < coarseTrails.size(); i ++) {
			bWriter.write("" + i + ":" + coarseTrails.get(i).getPoints().size() + "\n");
		}
		bWriter.close();
		
		File f2 = new File("fineTrails.txt");
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
	}
	
	public static void main(String[] args) throws Exception {
		//testTimeSegment();
		testCompress();
	}
}

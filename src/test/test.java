package test;

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
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 420*60*1000);
			int sum = 0;
			for(int j = 0; j < dividedTrail.size(); j ++) {
				if(dividedTrail.get(j).getSum_points() >= 10) {
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
	
	public ArrayList<Object> testCompress() throws Exception {
		ArrayList<Object> result = new ArrayList<>();
		ArrayList<Trail> trails = downloadData.getTrails("trail");
		ArrayList<Trail> finTrails = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Trail> dividedTrail = calculations.divideTrace(trails.get(i), 120*60*1000);
			ArrayList<Point> coarseTrail = calculations.coarseCompress(dividedTrail);
			Trail coarse_finTrail = new Trail();
			coarse_finTrail.setIMSI(trails.get(i).getIMSI());
			coarse_finTrail.setPoints(coarseTrail);
			coarse_finTrail.setSum_points(coarseTrail.size());
			coarse_finTrail.setTstart(coarseTrail.get(0).getDate());
			coarse_finTrail.setTend(coarseTrail.get(coarseTrail.size()-1).getDate());
			finTrails.add(coarse_finTrail);
		}
		result.add(trails);
		result.add(finTrails);
		return result;
	}
	
	public static void main(String[] args) {
		//testTimeSegment();
	}
}

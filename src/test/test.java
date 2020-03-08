package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;
import com.sun.javafx.collections.MappingChange.Map;

import calculation.calculations;
import process.downloadData;
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
	
	public ArrayList<Trail> testCompress() throws Exception {
		ArrayList<Trail> trails = downloadData.getTrails("trail");
		return trails;
	}
	
	public static void main(String[] args) {
		//testTimeSegment();
	}
}

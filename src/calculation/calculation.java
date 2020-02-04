package calculation;

import java.util.ArrayList;
import java.util.Date;

import com.sun.corba.se.impl.protocol.MinimalServantCacheLocalCRDImpl;
import com.sun.javafx.collections.MappingChange.Map;
import com.sun.org.apache.xpath.internal.operations.And;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import javafx.geometry.Pos;
import jdk.nashorn.internal.codegen.MethodEmitter;
import sun.awt.windows.WPrinterJob;
import sun.net.TelnetOutputStream;
import trail.Point;
import trail.Trail;

public class calculation {
	/**
	 * @author kyle_cloud
	 *
	 *粗粒度降维
	 */
	public ArrayList<Point> coarseCompress(ArrayList<Trail> trail, ArrayList<Integer> sum) throws Exception{
		ArrayList<Point> subp = new ArrayList<Point>(trail.size());
		for(int i = 0; i < trail.size(); i ++) {
			int sumLng = 0;
			int sumLat = 0;
			Trail subTra = trail.get(i);
			for(int j = 0; j < subTra.getPoints().size(); j ++) {
				sumLng += subTra.getPoints().get(j).getLng();
				sumLat += subTra.getPoints().get(j).getLat();
			}
			subp.get(i).setLng(sumLng / sum.get(i));
			subp.get(i).setLat(sumLat / sum.get(i));
			Date meanDate = new Date();
			meanDate = meanDate(subTra.getTstart(), subTra.getTend());
			subp.get(i).setDate(meanDate);
		}
		for(int i = 1; i < subp.size() - 1; i ++) {
			double angle = calAngle(subp.get(i), subp.get(i-1), subp.get(i+1));
			subp.get(i).setCor(angle);
		}
		return subp;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *细粒度降维
	 */
	public ArrayList<ArrayList<Object>> fineCompress(ArrayList<Trail> trail, Integer l, Integer lambda) {
		ArrayList<ArrayList<Object>> result = null;
		ArrayList<Point> minTra = null;
		Point minp = null;
		for(int i = 0; i < trail.size(); i ++) {
			Trail subTra = trail.get(i);
			minTra.add(subTra.getPoints().get(0));
			ArrayList<Point> points = subTra.getPoints();
			for(int j = 1; j < points.size(); j ++) {
				if(calcDistance(points.get(j), minTra.get(0)) >= l || calcDistOfDate(points.get(j), minTra.get(0)) >= lambda) {
					double sumTime = calcDistOfDate(minTra.get(0), minTra.get(minTra.size()-1));
					//求权重
					ArrayList<Double> wp = null;
					ArrayList<Point> qc_points = null;
					for(int k = 0; k < minTra.size(); k ++) {
						int stay = k;
						for(int h = k; h < minTra.size(); h ++) {//查看最后停留在此点的时间
							if(points.get(h).getLng() == points.get(k).getLng() && points.get(h).getLat() == points.get(k).getLat()) {
								stay = h;
							} else {
								break;
							}
						}
						double tp = 0;
						double lp = 0;
						if(stay == k) {
							if(k == 0 && minTra.size() > 1) {
								tp = calcDistOfDate(minTra.get(1), minTra.get(0)) / 2;
							} else if(k == 0 && minTra.size() == 1) {
								tp = lambda;
							} else if(k == minTra.size() && minTra.size() > 1) {
								tp = calcDistOfDate(minTra.get(k), minTra.get(k - 1)) / 2;
							} else {
								tp = calcDistOfDate(minTra.get(k + 1), minTra.get(k - 1)) / 2;
							}
						} else {
							tp = calcDistOfDate(points.get(stay), points.get(k)) / sumTime;
						}
						lp = (stay - k + 1) / minTra.size();
						wp.add(tp * lp);
						qc_points.add(minTra.get(k));
					}
					//求聚合点
					int qc_sum = qc_points.size();
					
					
					minTra.clear();
					minTra.add(points.get(j));
				} else {
					minTra.add(points.get(i));
				}
			}
		}
		return result;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *两点之间的距离
	 */
	public double calcDistance(Point first, Point second) {
		double ma_x = first.getLng() - second.getLng();
        double ma_y = first.getLat() - second.getLat();
        return Math.sqrt(ma_x * ma_x + ma_y * ma_y);
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *两点时间差,绝对值
	 */
	public double calcDistOfDate(Point p1, Point p2) {
		return Math.abs(p1.getDate().getTime() - p1.getDate().getTime());
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *平均时间
	 */
	public Date meanDate(Date d1, Date d2) {
		Date meanDate = new Date();
		meanDate.setTime((d1.getTime() + d2.getTime()) / 2);
		return meanDate;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *计算转角大小
	 */
	public double calAngle(Point cen, Point first, Point second) {
		double ma_x = first.getLng() - cen.getLng();
        double ma_y = first.getLat() - cen.getLat();
        double mb_x = second.getLng() - cen.getLng();
        double mb_y = second.getLat() - cen.getLat();
        double mc_x = first.getLng() - second.getLng();
        double mc_y = first.getLat() - second.getLat();
        double a2 = ma_x * ma_x + ma_y * ma_y;
        double b2 = mb_x * mb_x + mb_y * mb_y;
        double c2 = mc_x * mc_x + mc_y * mc_y;
        double a = Math.sqrt(a2);
        double b = Math.sqrt(b2);
        double angleAMB = Math.acos((a2 + b2 - c2) / (2 * a * b));
        return angleAMB;
    }
}

package calculation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.sun.javafx.tk.Toolkit.Task;
import com.sun.org.apache.xpath.internal.operations.And;

import trail.Point;
import trail.Trail;

public class calculations {
	/**
	 * @author kyle_cloud
	 *
	 *分时段划分轨迹
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Trail> divideTrace(Trail trail, long theta) {
		ArrayList<Trail> subTrails = new ArrayList<>();
		ArrayList<Date> segTimes = divideTime(theta, trail.getTstart(), trail.getTend());
//		System.out.println(trail.getPoints().get(0).getDate());
//		System.out.println(trail.getPoints().get(1).getDate());
//		System.out.println(trail.getPoints().get(0).getDate().getTime());
//		System.out.println(trail.getPoints().get(1).getDate().getTime());
		ArrayList<Point> points = trail.getPoints();
		ArrayList<Point> sub_points = new ArrayList<>();
		int current = 1;
		for(int i = 0; i < points.size(); i ++) {
			if(points.get(i).getDate().compareTo(segTimes.get(current)) >= 0) {
				current ++;
				Trail sub_trail = new Trail();
				sub_trail.setIMSI(trail.getIMSI());
				sub_trail.setPoints((ArrayList<Point>)sub_points.clone());
				sub_trail.setSum_points(sub_points.size());
				sub_trail.setTstart(sub_points.get(0).getDate());
				sub_trail.setTend(sub_points.get(sub_points.size()-1).getDate());
				subTrails.add(sub_trail);
				sub_points.clear();
			}
			sub_points.add(points.get(i));
		}
		//加进来最后一段
		Trail sub_trail = new Trail();
		sub_trail.setIMSI(trail.getIMSI());
		sub_trail.setPoints(sub_points);
		sub_trail.setSum_points(sub_points.size());
		sub_trail.setTstart(sub_points.get(0).getDate());
		sub_trail.setTend(sub_points.get(sub_points.size()-1).getDate());
		subTrails.add(sub_trail);
		return subTrails;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *粗粒度降维
	 *输入：一条轨迹
	 */
	public static ArrayList<Point> coarseCompress(ArrayList<Trail> trail) throws Exception{
		ArrayList<Point> subp = new ArrayList<>();
		for(int i = 0; i < trail.size(); i ++) {
			double sumLng = 0;
			double sumLat = 0;
			Trail subTra = trail.get(i);
			int sum_points = subTra.getPoints().size();
			for(int j = 0; j < subTra.getPoints().size(); j ++) {
				sumLng += subTra.getPoints().get(j).getLng();
				sumLat += subTra.getPoints().get(j).getLat();
			}
			Date meanDate = new Date();
			meanDate = meanDate(subTra.getTstart(), subTra.getTend());
			Point point_tmp = new Point();
			point_tmp.setLng(sumLng / sum_points);
			point_tmp.setLat(sumLat / sum_points);
			point_tmp.setDate(meanDate);
			subp.add(point_tmp);
		}
		for(int i = 1; i < subp.size() - 1; i ++) {
			double angle = calcAngle(subp.get(i), subp.get(i-1), subp.get(i+1));
			subp.get(i).setCor((double) Math.round(angle * 100000) / 100000);
		}
		return subp;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *细粒度降维
	 *输入：一条轨迹
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Trail> fineCompress(ArrayList<Trail> trail, double l, long lambda) {
		ArrayList<Trail> finTra = new ArrayList<>(); //最终轨迹
		ArrayList<Point> tmpTra = new ArrayList<>(); //子轨迹降维之后
		ArrayList<Point> minTra = new ArrayList<Point>();
		for(int i = 0; i < trail.size(); i ++) {
			Trail subTra = trail.get(i);
			ArrayList<Point> points = subTra.getPoints();
			minTra.clear();
			minTra.add(subTra.getPoints().get(0)); //存储每一个分段
			for(int j = 1; j < points.size(); j ++) {
				if(calcDistance(points.get(j), minTra.get(0)) >= l || calcDistOfDate(points.get(j), minTra.get(0)) >= lambda) {
					tmpTra.add(calcWeightedTogether(minTra, lambda));
					minTra.clear();
					minTra.add(points.get(j));
				} else {
					minTra.add(points.get(j));
				}
			}
			Trail tmpTrail = new Trail();
			//聚合一下minTra
			//加入tmpTra
			tmpTra.add(calcWeightedTogether(minTra, lambda));
			tmpTrail.setPoints((ArrayList<Point>)tmpTra.clone());
			tmpTrail.setSum_points(tmpTra.size());
			tmpTrail.setIMSI(trail.get(0).getIMSI());
			tmpTrail.setTstart(tmpTra.get(0).getDate());
			tmpTrail.setTend(tmpTra.get(tmpTra.size()-1).getDate());
			finTra.add(tmpTrail);
			tmpTra.clear();
		}
		return finTra;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *求权重并聚合
	 *输入：一条轨迹
	 */
	public static Point calcWeightedTogether(ArrayList<Point> minTra, long lambda) {
		Point minp = new Point();
		double sumTime = calcDistOfDate(minTra.get(0), minTra.get(minTra.size()-1));
		//求权重
		ArrayList<Double> wp = new ArrayList<>();
		ArrayList<Point> qc_points = new ArrayList<>();
		for(int k = 0; k < minTra.size(); k ++) {
			int stay = k;
			for(int h = k; h < minTra.size(); h ++) {//查看最后停留在此点的时间
				if(minTra.get(h).getLng() == minTra.get(k).getLng() && minTra.get(h).getLat() == minTra.get(k).getLat()) {
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
				} else if(k == minTra.size()-1 && minTra.size() > 1) {
					tp = calcDistOfDate(minTra.get(k), minTra.get(k - 1)) / 2;
				} else {
					tp = calcDistOfDate(minTra.get(k + 1), minTra.get(k - 1)) / 2;
				}
			} else {
				tp = calcDistOfDate(minTra.get(stay), minTra.get(k)) / sumTime;
			}
			lp = ((stay - k + 1) / (double)minTra.size());
			wp.add(tp * lp);
			qc_points.add(minTra.get(k));
			k = stay;
		}
		//求聚合点
		double minp_lng = 0;
		double minp_lat = 0;
		double minp_wp = 0;
		int qc_sum = qc_points.size();
		for(int k = 0; k < qc_sum; k ++) {
			minp_lng += qc_points.get(k).getLng() * wp.get(k);
			minp_lat += qc_points.get(k).getLat() * wp.get(k);
			minp_wp += wp.get(k);
		}
		minp.setLng(minp_lng / minp_wp);
		minp.setLat(minp_lat / minp_wp);
		minp.setDate(meanDate(minTra.get(0).getDate(), minTra.get(minTra.size()-1).getDate()));
		return minp;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *移动轨迹段提取（信息熵）
	 *输入：一条轨迹
	 */
	public static ArrayList<Trail> findTopk(ArrayList<Trail> trail, ArrayList<Integer> Sum, double belta) {
		ArrayList<Trail> topTra = new ArrayList<>();
		double hm = 0;
		int H_sum = 0;
		int H_num = 0;
		for(int i = 0; i < trail.size(); i ++) {
			hm = calcHm(trail.get(i), Sum.get(i));
			trail.get(i).setHm(hm);
			if(hm > 0) {
				H_sum ++;
				topTra.add(trail.get(i));
			}
		}
		H_num = (int) (H_sum * belta);
		topTra.sort(new Comparator<Trail>() {
            @Override
            public int compare(Trail t1, Trail t2) {
            	if(t1.getHm() > t2.getHm())
    				return 1;
    			return -1;
            }
        });
		List<Trail> sublist = topTra.subList(H_num, topTra.size());
        topTra.removeAll(sublist);
        topTra.sort(new Comparator<Trail>() {
            @Override
            public int compare(Trail t1, Trail t2) {
            	if(t1.getTstart().compareTo(t2.getTstart()) == 1)
    				return 1;
    			return -1;
            }
        });
		
		return topTra;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *相似度聚类
	 *输入：多条轨迹
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Trail> structCluster(ArrayList<Trail> trails, Trail objTrail, double alpha, double theta, int Minpts) {
		//ArrayList<Object> result = new ArrayList<>();
		trails.add(objTrail);
		ArrayList<Trail> cores = new ArrayList<>();
		ArrayList<ArrayList<Trail>> Ntheta = new ArrayList<>();
		ArrayList<Trail> N_tmp = new ArrayList<>();
		ArrayList<Trail> cluster = new ArrayList<>();
		ArrayList<Trail> noises = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			N_tmp.clear();
			for(int j = 0; j < trails.size(); j ++) {
				if(j == i) continue;
				double sim = calcSim(trails.get(i), trails.get(j), alpha);
				if(sim >= theta && j != i) {
					N_tmp.add(trails.get(j));
				}
			}
			if(N_tmp.size() >= Minpts) {
				cores.add(trails.get(i));
				Ntheta.add((ArrayList<Trail>) N_tmp.clone());
			}
		}
		int k = 0;
		for(int i = 0; i < cores.size(); i ++) {
			k ++;
			cores.get(i).setCluster_id(k);
			connectDensity(cores.get(i), cores, Ntheta, i, k);
		}
		//找出相似轨迹集
		int objCluster = objTrail.getCluster_id();
		trails.remove(objTrail);
		for(int i = 0; i < trails.size(); i ++) {
			if(trails.get(i).getCluster_id() == objCluster) {
				cluster.add(trails.get(i));
			}
			if(trails.get(i).getCluster_id() == 0) {
				noises.add(trails.get(i));
			}
		}
		return cluster;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *连通集标记
	 */
	public static void connectDensity(Trail core, ArrayList<Trail> cores, ArrayList<ArrayList<Trail>> N_trails, int index, int id) {
		for(int i = 0; i < N_trails.get(index).size(); i ++) {
			N_trails.get(index).get(i).setCluster_id(id);
			int index_tmp = cores.indexOf(N_trails.get(index).get(i));
			if(index_tmp != -1 && N_trails.get(index).get(i).getCluster_id() != id) {
				connectDensity(N_trails.get(index).get(i), cores, N_trails, index_tmp, id);
			}
		}
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *时间插值相似度计算
	 *输入：两条轨迹
	 */
	public static double innerSimilarity(ArrayList<Trail> topTra, ArrayList<Trail> finTra) {
		double H = 0;
		for(int i = 0; i < topTra.size(); i ++) {
			for(int j = 0; j < finTra.size(); j ++) {
				if(topTra.get(i).getTstart() != finTra.get(j).getTstart()) continue;
				Trail trail1 = new Trail();
				Trail trail1_copy = new Trail();
				Trail trail2 = new Trail();
				trail1 = topTra.get(i);
				trail1_copy = topTra.get(i);
				trail2 = finTra.get(j);
				Point pre_trail1 = null;	Point pre_trail2 = null;
				Point nxt_trail1 = null; Point nxt_trail2 = null;
				if(i > 0) pre_trail1 = topTra.get(i-1).getPoints().get(topTra.get(i-1).getPoints().size() - 1);
				if(j > 0) pre_trail2 = finTra.get(j-1).getPoints().get(finTra.get(j-1).getPoints().size() - 1);
				if(i < topTra.size()-1) nxt_trail1 = topTra.get(i+1).getPoints().get(0);
				if(j < finTra.size()-1) nxt_trail2 = finTra.get(j+1).getPoints().get(0);
				//insertPoints(trail1.getPoints(), trail2.getPoints(), pre_trail1, nxt_trail1);
				//insertPoints(trail2.getPoints(), trail1_copy.getPoints(), pre_trail2, nxt_trail2);
				H += calcHk(trail1.getPoints(), trail2.getPoints());
			}
		}
		int Pset = topTra.size();
		H /= Pset;
		return H;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *时间插值
	 *输入：两条子轨迹段
	 */
	public static void insertPoints(ArrayList<Point> points1, ArrayList<Point> points2, Point pre_1, Point nxt_1) {
		//在points1里插入points2的值
		int i = 0, j = 0;
		for(; i < points1.size(); i ++) {
			if(pre_1 == null) i ++;
			for(; j < points2.size(); j ++) {
				if(points2.get(j).getDate().getTime() <= pre_1.getDate().getTime()) { 
					j ++;
				} else if(points2.get(j).getDate().getTime() > points1.get(i).getDate().getTime()) {
					break;
				} else if(points2.get(j).getDate().getTime() == points1.get(i).getDate().getTime()) {
					j ++;
					break;
				} else {
					Point point_tmp = new Point();
					point_tmp.setLat((points1.get(i).getLat() + pre_1.getLat()) / 2);
					point_tmp.setLng((points1.get(i).getLng() + pre_1.getLng()) / 2);
					points1.add(i, point_tmp); i ++;
				}
			}
			pre_1 = points1.get(i);
		}
		if(nxt_1 != null && j < points2.size()) {
			for(; j < points2.size(); j ++) {
				if(points2.get(j).getDate().getTime() > pre_1.getDate().getTime() && points2.get(j).getDate().getTime() < nxt_1.getDate().getTime()) {
					Point point_tmp = new Point();
					point_tmp.setLat((nxt_1.getLat() + pre_1.getLat()) / 2);
					point_tmp.setLng((nxt_1.getLng() + pre_1.getLng()) / 2);
					points1.add(i, point_tmp); i ++;
				}
			}
		}
		//在points1里插入points2的值
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *计算子轨迹段Hausdorff
	 */
	public static double calcH(ArrayList<Trail> trail1, ArrayList<Trail> trail2) {
		double res = 0;
		for(int i = 0; i < trail1.size(); i ++) {
			res += calcHk(trail1.get(i).getPoints(), trail2.get(i).getPoints());
		}
		return res/trail1.size();
	}
	
	
	/**
	 * @author kyle_cloud
	 *
	 *计算子轨迹段Hausdorff
	 */
	public static double calcHk(ArrayList<Point> trail1, ArrayList<Point> trail2) {
		double min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;
		double max1 = Integer.MIN_VALUE, max2 = Integer.MIN_VALUE;
		for(int i = 0; i < trail1.size(); i ++) {
			for(int j = 0; j < trail2.size(); j ++) {
				min1 = Math.min(min1, calcDistance(trail1.get(i), trail2.get(j)));
			}
			max1 = Math.max(max1, min1);
		}
		for(int i = 0; i < trail2.size(); i ++) {
			for(int j = 0; j < trail1.size(); j ++) {
				min2 = Math.min(min2, calcDistance(trail2.get(i), trail1.get(j)));
			}
			max2 = Math.max(max2, min2);
		}
		return Math.max(max1, max2);
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *两点之间的距离
	 */
	public static double calcDistance(Point first, Point second) {
		double ma_x = first.getLng() - second.getLng();
        double ma_y = first.getLat() - second.getLat();
        return Math.sqrt(ma_x * ma_x + ma_y * ma_y);
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *分离时间段
	 */
	public static ArrayList<Date> divideTime(long theta, Date dStart, Date dEnd) {
		ArrayList<Date> segs = new ArrayList<>();
		segs.add(dStart);
		while(dStart.compareTo(dEnd) <= 0) {
			dStart = new Date(dStart.getTime() + theta);
			segs.add(dStart);
		}
		segs.add(new Date(dStart.getTime() + theta));
		return segs;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *两点时间差,绝对值
	 */
	public static long calcDistOfDate(Point p1, Point p2) {
		return Math.abs(p1.getDate().getTime() - p2.getDate().getTime());
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *平均时间
	 */
	public static Date meanDate(Date d1, Date d2) {
		Date meanDate = new Date();
		meanDate.setTime((d1.getTime() + d2.getTime()) / 2);
		return meanDate;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *计算转角大小
	 */
	public static double calcAngle(Point cen, Point first, Point second) {
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
        double angleAMB = 180 * Math.acos((a2 + b2 - c2) / (2 * a * b)) / Math.PI;
        return angleAMB;
    }
	
	/**
	 * @author kyle_cloud
	 *
	 *计算某一轨迹信息熵
	 */
	public static double calcHm(Trail trail, int sum) {
		double Hm = 0;
		ArrayList<Point> points = new ArrayList<>();
		points = trail.getPoints();
		points.sort(new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
            	if(p1.getLng() > p2.getLng())
    				return 1;
    			else if(p1.getLat() > p2.getLat())
    				return 1;
    			else return -1;
            }
        });
		for(int i = 0; i < points.size(); i ++) {
			int same_num = 1;
			for(int j = i; j < points.size(); j ++) {
				if(points.get(i).getLng() != points.get(j).getLng() || points.get(i).getLat() != points.get(j).getLat()) {
					break;
				}
				same_num ++;
			}
			double pi = same_num / sum;
			Hm -= pi * Math.log(pi) / Math.log(2);
			i += (same_num - 1);
		}
		return Hm;
	}	
	
	/**
	 * @author kyle_cloud
	 *
	 *两轨迹相似度
	 */
	public static double calcSim(Trail trail1, Trail trail2, double alpha) {
		return 1 - alpha * calcLocD(trail1, trail2) + (1 - alpha) * calcAngleD(trail1, trail2);
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *两轨迹位置距离
	 */
	public static double calcLocD(Trail trail1, Trail trail2) {
		double res = 0;		double tmp = 0;
		double min = Integer.MAX_VALUE;		double max = 0;
		for(int i = 0; i < trail1.getPoints().size() && i < trail2.getPoints().size(); i ++) {
			tmp = calcDistance(trail1.getPoints().get(i), trail2.getPoints().get(i));
			min = Math.min(min, tmp);
			max = Math.max(max, tmp);
			res += tmp;
		}
		res /= trail1.getPoints().size();
		res = (res - min) / (max - min);
		return res;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *两轨迹形状距离
	 */
	public static double calcAngleD(Trail trail1, Trail trail2) {
		double res = 0;		double tmp = 0;
		double min = Integer.MAX_VALUE;		double max = 0;
		for(int i = 0; i < trail1.getPoints().size() && i < trail2.getPoints().size(); i ++) {
			tmp = Math.abs(trail1.getPoints().get(i).getCor() - trail2.getPoints().get(i).getCor());
			min = Math.min(min, tmp);
			max = Math.max(max, tmp);
			res += tmp;
		}
		res /= trail1.getPoints().size();
		res = (res - min) / (max - min);
		return res;
	}
}

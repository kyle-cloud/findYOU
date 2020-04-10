package calculation;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.objectmatrix.calculation.Repmat;

import com.sun.mail.util.TraceInputStream;
import com.sun.org.apache.xpath.internal.operations.And;

import process.downloadData;
import trail.Point;
import trail.Trail;


import com.mathworks.toolbox.javabuilder.MWArray;  
import com.mathworks.toolbox.javabuilder.MWClassID;  
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray; 

public class calculations {
	
	
	
	/**
	 * @author kyle_cloud
	 *
	 *分时段划分轨迹
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Trail> divideTrace(Trail trail, long theta) {
		ArrayList<Trail> subTrails = new ArrayList<>();
		ArrayList<Long> segTimes = divideTime(theta, trail.getTstart(), trail.getTend());
//		System.out.println(trail.getPoints().get(0).getDate());
//		System.out.println(trail.getPoints().get(1).getDate());
//		System.out.println(trail.getPoints().get(0).getDate().getTime());
//		System.out.println(trail.getPoints().get(1).getDate().getTime());
		ArrayList<Point> points = trail.getPoints();
		ArrayList<Point> sub_points = new ArrayList<>();
		int current = 1;
		for(int i = 0; i < points.size(); i ++) {
			if(points.get(i).getDate() >= segTimes.get(current)) {
				current ++;
				Trail sub_trail = new Trail();
				sub_trail.setID(trail.getID());
				sub_trail.setIMSI(trail.getIMSI());
				sub_trail.setHm_index(current - 2);
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
		sub_trail.setID(trail.getID());
		sub_trail.setIMSI(trail.getIMSI());
		sub_trail.setHm_index(current - 1);
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
			long meanDate;
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
			tmpTrail.setID(trail.get(0).getID());
			tmpTrail.setTstart(tmpTra.get(0).getDate());
			tmpTrail.setTend(tmpTra.get(tmpTra.size()-1).getDate());
			tmpTrail.setHm_index(i);
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
	 *求权重并聚合_改进版本_兴趣点factor
	 *输入：一条轨迹
	 */
	public static Point calcWeightedTogetherAdvanced(ArrayList<Point> minTra, long lambda) {
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
	 *目标移动轨迹段提取（信息熵）
	 *输入：一条轨迹
	 * @throws CloneNotSupportedException 
	 */
	public static ArrayList<Object> findTopk(ArrayList<Trail> trail, double belta) throws CloneNotSupportedException {
//		ArrayList<Trail> trail = new ArrayList<>();
//		for(int i = 0; i < trail_in.size(); i ++) {
//			Trail temp = trail_in.get(i).clone();
//			trail.add(temp);
//		}
		ArrayList<Object> result = new ArrayList<>();
		ArrayList<Trail> topTra = new ArrayList<>();
		ArrayList<Integer> topIndex = new ArrayList<>();
		double hm = 0;
		int H_sum = 0;
		int H_num = 0;	
		//在这里段内的点发生了顺序改变(因为calcHm有点排序)
		for(int i = 0; i < trail.size(); i ++) {
			hm = calcHm(trail.get(i), trail.get(i).getPoints().size());
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
            	if(t1.getHm() < t2.getHm())
    				return 1;
            	else if(t1.getHm() == t2.getHm())
            		return 0;
    			return -1;
            }
        });
		List<Trail> sublist = topTra.subList(H_num, topTra.size());
        topTra.removeAll(sublist);
        topTra.sort(new Comparator<Trail>() {
            @Override
            public int compare(Trail t1, Trail t2) {
            	if(t1.getHm_index() > t2.getHm_index())
    				return 1;
            	else if(t1.getHm_index() == t2.getHm_index())
            		return 0;
    			return -1;
            }
        });
		
        for(int i = 0; i < topTra.size(); i ++) {
        	topIndex.add(topTra.get(i).getHm_index());
        }
        result.add(topTra);
        result.add(topIndex);
		return result;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *比较移动轨迹段提取
	 *输入：一条轨迹
	 */
	public static ArrayList<Trail> getTopk(ArrayList<Trail> trails, ArrayList<Integer> indexes) {
		ArrayList<Trail> topTra = new ArrayList<>();
		for(int i = 0; i < indexes.size(); i ++) {
			if(indexes.get(i) >= trails.size()) break;
			topTra.add(trails.get(indexes.get(i)));
		}
		return topTra;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *相似度聚类
	 *输入：多条轨迹
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Integer> structCluster(ArrayList<Trail> trails, double alpha, double theta, int Minpts) {
		//ArrayList<Object> result = new ArrayList<>();
		//trails.add(objTrail);//我的目标轨迹例子拿的就是里边的一条轨迹，再加上一次，之后remove只会去掉一个
		ArrayList<Trail> cores = new ArrayList<>();
		ArrayList<ArrayList<Integer>> Ntheta = new ArrayList<>();
		ArrayList<Integer> N_tmp = new ArrayList<>();
		//ArrayList<Integer> cluster = new ArrayList<>();
		ArrayList<Integer> noises = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			N_tmp.clear();
			for(int j = 0; j < trails.size(); j ++) {
				if(j == i) continue;
				double sim = calcSim(trails.get(i), trails.get(j), alpha);
				//if(i == 44 && j >= 44 && j < 52) System.out.println(sim); //简单测试了一下相似轨迹的相似数值范围
				if(sim >= theta && j != i) {
					N_tmp.add(j);
				}
			}
			System.out.println(i + " : " + N_tmp.size());
//			if(N_tmp.size() > trails.size() - 20) {
//				System.out.println(i + " " + N_tmp.size());
//				System.out.println(trails.get(i).getIMSI());
//			}
			if(N_tmp.size() >= Minpts && N_tmp.size() <= trails.size() / 2) {
				cores.add(trails.get(i));
				Ntheta.add((ArrayList<Integer>) N_tmp.clone());
			}
		}
		int k = 0;
		for(int i = 0; i < cores.size(); i ++) {
			if(cores.get(i).getCluster_id() != 0) continue;
			k ++;
			cores.get(i).setCluster_id(k);
			connectDensity(cores.get(i), trails, cores, Ntheta, i, k);
		}
		for(int i = 0; i < trails.size(); i ++) {
			if(trails.get(i).getCluster_id() == 0) {
				noises.add(i);
			}
		}
		return noises;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *连通集标记
	 */
	public static void connectDensity(Trail core, ArrayList<Trail> trails, ArrayList<Trail> cores, ArrayList<ArrayList<Integer>> N_trails, int index, int id) {
		for(int i = 0; i < N_trails.get(index).size(); i ++) {
			if(trails.get(N_trails.get(index).get(i)).getCluster_id() == id)
				continue;
			trails.get(N_trails.get(index).get(i)).setCluster_id(id);
			int index_tmp = cores.indexOf(trails.get(N_trails.get(index).get(i)));
			if(index_tmp != -1) {
				connectDensity(trails.get(N_trails.get(index).get(i)), trails, cores, N_trails, index_tmp, id);
			}
		}
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *时间插值相似度计算
	 *输入：两条轨迹
	 * @throws CloneNotSupportedException 
	 * @throws MWException 
	 */
	public static double innerSimilarity(ArrayList<Trail> topTra, ArrayList<Trail> finTra) throws CloneNotSupportedException, MWException {
		double H = 0;
		for(int i = 0; i < topTra.size() && i < finTra.size(); i ++) {
				//if(topTra.get(i).getTstart() != finTra.get(j).getTstart()) continue;
				Trail trail1 = new Trail();
				//Trail trail1_copy = new Trail();
				Trail trail2 = new Trail();

				trail1 = topTra.get(i).clone();
				//trail1_copy = topTra.get(i); // 还是地址，没用这句话，应该clone。但是不影响结果
				trail2 = finTra.get(i).clone();
				Point pre_trail1 = null;	Point pre_trail2 = null;
				Point nxt_trail1 = null; Point nxt_trail2 = null;
				if(i > 0) pre_trail1 = topTra.get(i-1).getPoints().get(topTra.get(i-1).getPoints().size() - 1);
				if(i > 0) pre_trail2 = finTra.get(i-1).getPoints().get(finTra.get(i-1).getPoints().size() - 1);
				if(i < topTra.size()-1) nxt_trail1 = topTra.get(i+1).getPoints().get(0);
				if(i < finTra.size()-1) nxt_trail2 = finTra.get(i+1).getPoints().get(0);
				insertPoints(trail1.getPoints(), trail2.getPoints(), pre_trail1, nxt_trail1);
				insertPoints(trail2.getPoints(), trail1.getPoints(), pre_trail2, nxt_trail2);
				//看一下插入的结果
				H += calcHk(trail1.getPoints(), trail2.getPoints());
		}
		int Pset = topTra.size();
		H /= (double)Pset;
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
			if(pre_1 == null) {
				pre_1 = points1.get(0);
				continue;
			}
			for(; j < points2.size(); j ++) {
				//System.out.println(j);
				if(points2.get(j).getDate() <= pre_1.getDate()) { 
					j ++;
				} else if(points2.get(j).getDate() > points1.get(i).getDate()) {
					break;
				} else if(points2.get(j).getDate() == points1.get(i).getDate()) {
					j ++;
					break;
				} else {
					Point point_tmp = new Point();
					point_tmp.setDate(points2.get(j).getDate());
					point_tmp.setLat((points1.get(i).getLat() + pre_1.getLat()) / 2);
					point_tmp.setLng((points1.get(i).getLng() + pre_1.getLng()) / 2);
					points1.add(i, point_tmp); i ++;
				}
			}
			pre_1 = points1.get(i);
		}
		if(nxt_1 != null && j < points2.size()) {
			for(; j < points2.size(); j ++) {
				if(points2.get(j).getDate() > pre_1.getDate() && points2.get(j).getDate() < nxt_1.getDate()) {
					Point point_tmp = new Point();
					point_tmp.setDate(points2.get(j).getDate());
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
	 *计算轨迹段Hausdorff
	 * @throws MWException 
	 */
	public static double calcH(ArrayList<Trail> trail1, ArrayList<Trail> trail2) throws MWException {
		double res = 0;
		for(int i = 0; i < trail1.size() && i < trail2.size(); i ++) {
			res += calcHk(trail1.get(i).getPoints(), trail2.get(i).getPoints());
		}
		return res/trail1.size();
	}
	
	
	/**
	 * @author kyle_cloud
	 *
	 *计算子轨迹段Hausdorff
	 * @throws MWException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static double calcHk(ArrayList<Point> trail1, ArrayList<Point> trail2) {
// 		Matrix p1 = DenseMatrix.Factory.zeros(trail1.size(), 2);
// 		Matrix p2 = DenseMatrix.Factory.zeros(trail2.size(), 2);
// 		for(int i = 0; i < trail1.size(); i ++) {
// 			p1.setAsDouble(trail1.get(i).getLat(), i, 0);
// 			p1.setAsDouble(trail1.get(i).getLng(), i, 1);
// 		}
// 		for(int i = 0; i < trail2.size(); i ++) {
// 			p2.setAsDouble(trail2.get(i).getLat(), i, 0);
// 			p2.setAsDouble(trail2.get(i).getLng(), i, 1);
// 		}
// 		Matrix dMatrix = DenseMatrix.Factory.zeros(trail1.size(), 1);
// 	    for(int i = 0; i < trail1.size(); i ++) {
// 	    	Matrix tMatrix = p2.minus(p1.selectRows(Ret.LINK, i));
// 	    	dMatrix.setAsDouble(tMatrix.selectColumns(Ret.LINK, 0).times(tMatrix.selectColumns(Ret.LINK, 0)).plus(tMatrix.selectColumns(Ret.LINK, 0).times(tMatrix.selectColumns(Ret.LINK, 0))).getMinValue(), i, 0);
// 	    }
// 		
// 		return 1;

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
		return Distance(first.getLat(), first.getLng(), second.getLat(), second.getLng());
	}
    /// <returns>距离（公里、千米）</returns>
    public static double Distance(double lat1,double lon1, double lat2,double lon2) {
    	double EARTH_RADIUS = 6371.0;
        //用haversine公式计算球面两点间的距离。
        //经纬度转换成弧度
        lat1 = ConvertDegreesToRadians(lat1);
        lon1 = ConvertDegreesToRadians(lon1);
        lat2 = ConvertDegreesToRadians(lat2);
        lon2 = ConvertDegreesToRadians(lon2);
        //差值
        double vLon = Math.abs(lon1 - lon2);
        double vLat = Math.abs(lat1 - lat2);
        //h is the great circle distance in radians, great circle就是一个球体上的切面，它的圆心即是球心的一个周长最大的圆。
        double h = HaverSin(vLat) + Math.cos(lat1) * Math.cos(lat2) * HaverSin(vLon);
        double distance = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(h));
        return distance;
    } 
    public static double HaverSin(double theta)
    {
        double v = Math.sin(theta / 2);
        return v * v;
    }
    /// 将角度换算为弧度。
    public static double ConvertDegreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }
    public static double ConvertRadiansToDegrees(double radian) {
        return radian * 180.0 / Math.PI;
    }
	
	/**
	 * @author kyle_cloud
	 *
	 *分离时间段
	 */
	public static ArrayList<Long> divideTime(long theta, long dStart, long dEnd) {
		ArrayList<Long> segs = new ArrayList<>();
		segs.add(dStart);
		while(dStart < dEnd) {
			dStart = dStart + theta;
			segs.add(dStart);
		}
		segs.add(dStart + theta);
		return segs;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *两点时间差,绝对值
	 */
	public static long calcDistOfDate(Point p1, Point p2) {
		return Math.abs(p1.getDate() - p2.getDate());
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *平均时间
	 */
	public static long meanDate(long d1, long d2) {
		long meanDate;
		meanDate = (d1 + d2) / 2;
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
	public static double calcHm(Trail trail_in, int sum) {
		Trail trail = trail_in.clone();
		double Hm = 0;
		ArrayList<Point> points = new ArrayList<>();
		points = trail.getPoints();
		points.sort(new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
            	if(p1.getLng() > p2.getLng())
    				return 1;
            	else if(p1.getLng() < p2.getLng())
            		return -1;
    			else if(p1.getLat() > p2.getLat())
    				return 1;
    			else if(p1.getLat() < p2.getLat())
    				return -1;
    			else return 0;
            }
        });
		for(int i = 0; i < points.size(); i ++) {
			int same_num = 1;
			for(int j = i + 1; j < points.size(); j ++) {
				if(points.get(i).getLng() != points.get(j).getLng() || points.get(i).getLat() != points.get(j).getLat()) {
					break;
				}
				same_num ++;
			}
			double pi = (double)same_num / sum;
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
		return 1 - alpha * calcLocD(trail1, trail2) - (1 - alpha) * calcAngleD(trail1, trail2);
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
		if(max == min) return 0;
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
		if(max == min) return 0;
		res /= trail1.getPoints().size();
		res = (res - min) / (max - min);
		return res;
	}
}

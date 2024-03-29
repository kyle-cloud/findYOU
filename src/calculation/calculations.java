package calculation;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.math3.analysis.function.Min;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.objectmatrix.calculation.Repmat;

import com.sun.mail.util.TraceInputStream;
import com.sun.org.apache.xpath.internal.operations.And;

import process.downloadData;
import trail.Line;
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
	 *MDL划分
	 *输入：一条轨迹
	 */
	public static ArrayList<Point> MDLpartion(ArrayList<Point> points) {
		ArrayList<Point> cPoints = new ArrayList<>();
		cPoints.add(points.get(0));
		int startIndex = 0;
		int length = 1;
		while(startIndex + length < points.size()) {
			int curIndex = startIndex + length;
			double cost_nopar = Math.log(calcDistance(points.get(startIndex), points.get(curIndex))) / Math.log(2);
			double cost_par = calcMDL(points, startIndex, curIndex);
			if(cost_par > cost_nopar) {
				cPoints.add(points.get(curIndex - 1));
				startIndex = curIndex - 1;
				length = 1;
			} else {
				length ++;
			}
		}
		cPoints.add(points.get(points.size() - 1));
		return cPoints;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *计算MDL
	 *输入：两个点
	 */
	public static double calcMDL(ArrayList<Point> points, int startIndex, int curIndex) {
		double sum_distance = 0;
		double sum_angle = 0;
		for(int i = startIndex; i < curIndex; i ++) { //好像不是这样，得算已经在c里边的特征点
			ArrayList<Double> result = calcXianduanDistance(points.get(startIndex), points.get(curIndex), points.get(i), points.get(i + 1));
			sum_distance += result.get(0);
			sum_angle += result.get(1);
		}
		return Math.log(calcDistance(points.get(startIndex), points.get(curIndex))) / Math.log(2) + Math.log(sum_distance) / Math.log(2) + Math.log(sum_angle) / Math.log(2);
	}
	
	public static ArrayList<Double> calcXianduanDistance(Point p11, Point p12, Point p21, Point p22) {
		ArrayList<Double> result = new ArrayList<>();
		double dx = p11.getLng() - p12.getLng();
		double dy = p11.getLat() - p12.getLat();
		
		double m1 = (p21.getLng() - p11.getLng()) * dx + (p21.getLat() - p11.getLat()) * dy;
		double m2 = (p22.getLng() - p11.getLng()) * dx + (p22.getLat() - p11.getLat()) * dy;
		m1 /= dx*dx + dy*dy;
		m2 /= dx*dx + dy*dy;
		
		Point cross1 = new Point();
		cross1.setLng(p11.getLng() + m1 * dx);
		cross1.setLat(p11.getLat() + m1 * dy);
		Point cross2 = new Point();
		cross2.setLng(p11.getLng() + m2 * dx);
		cross2.setLat(p11.getLat() + m2 * dy);
		double l1 = calcDistance(p21, cross1);
		double l2 = calcDistance(p22, cross2);
		double angle = 0;
		if(l1 == 0 && l2 == 0) {
			result.add(0.0);
			result.add(0.0);
			result.add(Math.min(calcDistance(p11, cross1), calcDistance(p12, cross2)));
			return result;
		} else if(l2 == 0) {
			angle = calcAngle(p21, cross2, p11);
		} else {
			Point cross_angle = new Point();
			cross_angle.setLng(cross2.getLng() - l1/l2*(cross2.getLng() - p22.getLng()));
			cross_angle.setLat(cross2.getLat() - l1/l2*(cross2.getLat() - p22.getLat()));
			angle = calcAngle(p22, p21, cross_angle);
		}
		
		double d_chuizhi = (l1*l1 + l2*l2) / (l1 + l2);
		double d_angle = l2 - l1;
		if(angle >= 90 && angle <= 180) d_angle = calcDistance(p21, p22);
		result.add(d_chuizhi);
		result.add(d_angle);
		result.add(Math.min(calcDistance(p11, cross1), calcDistance(p12, cross2)));
		return result;
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
		getInterestWeight(trail);
		for(int i = 0; i < trail.size(); i ++) {
			Trail subTra = trail.get(i);
			ArrayList<Point> points = subTra.getPoints();
			minTra.clear();
			minTra.add(subTra.getPoints().get(0)); //存储每一个分段
			for(int j = 1; j < points.size(); j ++) {
				if(calcDistance(points.get(j), minTra.get(0)) >= l || calcDistOfDate(points.get(j), minTra.get(0)) >= lambda) {
					tmpTra.add(calcWeightedTogetherAdvanced(minTra, lambda));
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
	 *求兴趣点
	 *输入：一条轨迹
	 */
	public static void getInterestWeight(ArrayList<Trail> trails) {
		ArrayList<Point> points = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			points.addAll(trails.get(i).getPoints());
		}
		for(int i = 0; i < points.size(); i ++) {
			int num = 0;
			for(int j = 0; j < points.size(); j ++) {
				if(calcDistance(points.get(i), points.get(j)) < 1000) {
					num ++;
				}
			}
			points.get(i).setWeightInterest((double)num / (double)points.size());
		}
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
			wp.add(tp * lp * minTra.get(k).getWeightInterest());
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
	public static ArrayList<Trail> structCluster(ArrayList<Trail> trails, double alpha, double theta, int Minpts) {
		//ArrayList<Object> result = new ArrayList<>();
		//trails.add(objTrail);//我的目标轨迹例子拿的就是里边的一条轨迹，再加上一次，之后remove只会去掉一个
		ArrayList<Trail> cores = new ArrayList<>();
		ArrayList<ArrayList<Trail>> Ntheta = new ArrayList<>();
		//ArrayList<Integer> cluster = new ArrayList<>();
		ArrayList<Trail> noises = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Trail> N_tmp = new ArrayList<>();
			for(int j = 0; j < trails.size(); j ++) {
				if(j == i) continue;
				double sim = calcSim(trails.get(i), trails.get(j), alpha);
				//System.out.println(sim); //简单测试了一下相似轨迹的相似数值范围
				if(sim >= theta && j != i) {
					N_tmp.add(trails.get(j));
				}
			}
//			System.out.println(i + " : " + N_tmp.size());
//			if(N_tmp.size() > trails.size() - 20) {
//				System.out.println(i + " " + N_tmp.size());
//				System.out.println(trails.get(i).getIMSI());
//			}
			if(N_tmp.size() >= Minpts && N_tmp.size() <= trails.size() / 2) {
				cores.add(trails.get(i));
				Ntheta.add(N_tmp);
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
				noises.add(trails.get(i));
			}
		}
		return noises;
	}
	
	public static void Traclus() {
		ArrayList<Trail> trails = new ArrayList<>();
		ArrayList<Line> lines = new ArrayList<>();
		trails.addAll(downloadData.getTrails("testTrail_coarse"));
		trails.addAll(downloadData.getTrails("trail_coarse"));
		for(int i = 0; i < trails.size(); i ++) {
			ArrayList<Point> points = trails.get(i).getPoints();
			for(int j = 0; j < points.size() - 1; j ++) {
				Line line = new Line();
				line.setStart_point(points.get(j));
				line.setEnd_point(points.get(j + 1));
				line.setTrail_id(trails.get(i).getID());
				lines.add(line);
			}
		}
		ArrayList<ArrayList<Line>> clusters = new ArrayList<>();
		//clusters = DBSCANCluster
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *连通集标记
	 */
	public static void connectDensity(Trail core, ArrayList<Trail> trails, ArrayList<Trail> cores, ArrayList<ArrayList<Trail>> N_trails, int index, int id) {
		Queue<Trail> queue = new LinkedBlockingQueue<>();
		queue.addAll(N_trails.get(index));
		while(!queue.isEmpty()) {
			Trail curTrail = queue.poll();
			int index_temp = cores.indexOf(curTrail);
			if(index_temp == -1) {
				curTrail.setCluster_id(id);
			} else if(curTrail.getCluster_id() == 0) {
				curTrail.setCluster_id(id);
				queue.addAll(N_trails.get(index_temp));
			}
		}
//		for(int i = 0; i < N_trails.get(index).size(); i ++) {
//			if(trails.get(N_trails.get(index).get(i)).getCluster_id() == id)
//				continue;
//			trails.get(N_trails.get(index).get(i)).setCluster_id(id);
//			int index_tmp = cores.indexOf(trails.get(N_trails.get(index).get(i)));
//			if(index_tmp != -1) {
//				connectDensity(trails.get(N_trails.get(index).get(i)), trails, cores, N_trails, index_tmp, id);
//			}
//		}
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
		double min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;
		double max1 = Integer.MIN_VALUE, max2 = Integer.MIN_VALUE;
		Point p1_min = new Point(); Point p2_min = new Point();
		Point p1_max = new Point(); Point p2_max = new Point();
		for(int i = 0; i < trail1.size(); i ++) {
			for(int j = i - 1; j >= 0 && j <= i + 1 && j < trail2.size(); j ++) {
				double temp = calcDistanceSimple(trail1.get(i), trail2.get(j));
				if(temp < min1) {
					p1_min = trail1.get(i); p2_min = trail2.get(j);
					min1 = temp;
				}
			}
			if(i == 0) {
				if(trail2.size() > 0) {
					double temp = calcDistanceSimple(trail1.get(i), trail2.get(0));
					if(temp < min1) {
						p1_min = trail1.get(i); p2_min = trail2.get(0);
						min1 = temp;
					}
				}
				if(trail2.size() > 1) {
					double temp = calcDistanceSimple(trail1.get(i), trail2.get(1));
					if(temp < min1) {
						p1_min = trail1.get(i); p2_min = trail2.get(1);
						min1 = temp;
					}
				}
				if(trail2.size() > 2) {
					double temp = calcDistanceSimple(trail1.get(i), trail2.get(2));
					if(temp < min1) {
						p1_min = trail1.get(i); p2_min = trail2.get(2);
						min1 = temp;
					}
				}
			}
			if(min1 > max1) {
				p1_max = p1_min; p2_max = p2_min;
				max1 = min1;
			}
		}
//		for(int i = 0; i < trail2.size(); i ++) {
//			for(int j = i - 1; j >= 0 && j <= i + 1 && j < trail1.size(); j ++) {
//				min2 = Math.min(min2, calcDistance(trail2.get(i), trail1.get(j)));
//			}
//			if(i == 0) {
//				if(trail1.size() > 0) min2 = Math.min(min1, calcDistance(trail2.get(i), trail1.get(0)));
//				if(trail1.size() > 1) min2 = Math.min(min1, calcDistance(trail2.get(i), trail1.get(1)));
//				if(trail1.size() > 2) min2 = Math.min(min1, calcDistance(trail2.get(i), trail1.get(2)));
//			}
//			max2 = Math.max(max2, min2);
//		}
//		
//		return Math.max(max1, max2);
		return calcDistance(p1_max, p2_max);
	}
	
	public static double calcHk_former(ArrayList<Point> trail1, ArrayList<Point> trail2) {
		double min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;
		double max1 = Integer.MIN_VALUE, max2 = Integer.MIN_VALUE;
		Point p1_min = new Point(); Point p2_min = new Point();
		Point p1_max = new Point(); Point p2_max = new Point();
		for(int i = 0; i < trail1.size(); i ++) {
			for(int j = 0; j < trail2.size(); j ++) {
				double temp = calcDistanceSimple(trail1.get(i), trail2.get(j));
				if(temp < min1) {
					p1_min = trail1.get(i); p2_min = trail2.get(j);
					min1 = temp;
				}
			}
			if(min1 > max1) {
				p1_max = p1_min; p2_max = p2_min;
				max1 = min1;
			}
		}
		Point p1_min_2 = new Point(); Point p2_min_2 = new Point();
		Point p1_max_2 = new Point(); Point p2_max_2 = new Point();
		for(int i = 0; i < trail2.size(); i ++) {
			for(int j = 0; j < trail1.size(); j ++) {
				double temp = calcDistanceSimple(trail2.get(i), trail1.get(j));
				if(temp < min2) {
					p1_min_2 = trail2.get(i); p2_min_2 = trail1.get(j);
					min2 = temp;
				}
			}
			if(min2 > max2) {
				p1_max_2 = p1_min_2; p2_max_2 = p2_min_2;
				max2 = min2;
			}
		}
		return Math.max(calcDistance(p1_max, p2_max), calcDistance(p1_max_2, p2_max_2));
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *两点之间的距离
	 */
	public static double calcDistanceSimple(Point first, Point second) {
		return Math.pow((first.getLat()-second.getLat()), 2) + Math.pow((first.getLng()-second.getLng()), 2);
	}
	
	public static double calcDistance(Point first, Point second) {
		return Distance(first.getLat(), first.getLng(), second.getLat(), second.getLng());
	}
	public static double Distance(double sLat, double sLng, double eLat, double eLng) {
        double x,y,out;
        double PI=Math.PI;
        double EARTH_RADIUS = 6.371229*1e6;
        x=(eLat-sLat)* PI * EARTH_RADIUS * Math.cos( ((sLng+eLng)/2) * PI /180)/180;
        y=(eLng-sLng)* PI * EARTH_RADIUS / 180;
        out=Math.hypot(x,y);
        return out;
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

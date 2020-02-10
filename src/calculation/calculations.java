package calculation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import trail.Point;
import trail.Trail;

public class calculations {
	/**
	 * @author kyle_cloud
	 *
	 *粗粒度降维
	 */
	public ArrayList<Point> coarseCompress(ArrayList<Trail> trail, ArrayList<Integer> sum) throws Exception{
		ArrayList<Point> subp = new ArrayList<>(trail.size());
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
	public ArrayList<Object> fineCompress(ArrayList<Trail> trail, Integer l, Integer lambda) {
		ArrayList<Object> result = new ArrayList<>();
		ArrayList<Trail> finTra = new ArrayList<>(); //最终轨迹
		ArrayList<Point> tmpTra = new ArrayList<>(); //子轨迹降维之后
		ArrayList<Integer> fSum = new ArrayList<>(); //最终轨迹每段点个数
		int fsum = 0; //子轨迹降维后的点个数
		ArrayList<Point> minTra = new ArrayList<Point>();
		Point minp = new Point();
		for(int i = 0; i < trail.size(); i ++) {
			Trail subTra = trail.get(i);
			ArrayList<Point> points = subTra.getPoints();
			minTra.add(subTra.getPoints().get(0)); //存储每一个分段
			for(int j = 1; j < points.size(); j ++) {
				if(calcDistance(points.get(j), minTra.get(0)) >= l || calcDistOfDate(points.get(j), minTra.get(0)) >= lambda) {
					double sumTime = calcDistOfDate(minTra.get(0), minTra.get(minTra.size()-1));
					//求权重
					ArrayList<Double> wp = new ArrayList<>();
					ArrayList<Point> qc_points = new ArrayList<>();
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
					tmpTra.add(minp);
					fsum ++;
					
					minTra.clear();
					minTra.add(points.get(j));
				} else {
					minTra.add(points.get(j));
				}
			}
			Trail tmpTrail = new Trail();
			tmpTrail.setPoints(tmpTra);
			finTra.add(tmpTrail);
			fSum.add(fsum);
			tmpTra.clear();
			fsum = 0;
		}
		result.add(finTra);
		result.add(fsum);
		return result;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *移动轨迹段提取（信息熵）
	 */
	public ArrayList<Trail> findTopk(ArrayList<Trail> trail, ArrayList<Integer> Sum, double belta) {
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
	 */
	public ArrayList<Trail> structCluster(ArrayList<Trail> trails, double alpha, double theta, int Minpts) {
		//ArrayList<Object> result = new ArrayList<>();
		ArrayList<Trail> cores = new ArrayList<>();
		ArrayList<ArrayList<Trail>> Ntheta = new ArrayList<>();
		ArrayList<Trail> N_tmp = new ArrayList<>();
		for(int i = 0; i < trails.size(); i ++) {
			N_tmp.clear();
			for(int j = 0; j < trails.size(); j ++) {
				double sim = calcSim(trails.get(i), trails.get(j), alpha);
				if(sim >= theta && j != i) {
					N_tmp.add(trails.get(j));
				}
			}
			if(N_tmp.size() >= Minpts) {
				cores.add(trails.get(i));
				Ntheta.add(N_tmp);
			}
		}
		int k = 0;
		for(int i = 0; i < cores.size(); i ++) {
			k ++;
			cores.get(i).setCluster_id(k);
			connectDensity(cores.get(i), cores, Ntheta, i, k);
		}
		return cores;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *时间插值相似度计算
	 */
	public double innerSimilarity(ArrayList<Trail> topTra, ArrayList<Trail> finTra) {
		for(int i = 0; i < topTra.size(); i ++) {
			for(int j = 0; j < finTra.size(); j ++) {
				
			}
		}
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
	
	/**
	 * @author kyle_cloud
	 *
	 *计算某一轨迹信息熵
	 */
	public double calcHm(Trail trail, int sum) {
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
	 *连通集标记
	 */
	public void connectDensity(Trail core, ArrayList<Trail> cores, ArrayList<ArrayList<Trail>> N_trails, int index, int id) {
		for(int i = 0; i < N_trails.get(index).size(); i ++) {
			N_trails.get(index).get(i).setCluster_id(id);
			int index_tmp = cores.indexOf(N_trails.get(index).get(i));
			if(index_tmp != -1) {
				connectDensity(N_trails.get(index).get(i), cores, N_trails, index_tmp, id);
			}
		}
	}
	
	
	/**
	 * @author kyle_cloud
	 *
	 *两轨迹相似度
	 */
	public double calcSim(Trail trail1, Trail trail2, double alpha) {
		return 1 - alpha * calcLocD(trail1, trail2) + (1 - alpha) * calcAngleD(trail1, trail2);
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *两轨迹位置距离
	 */
	public double calcLocD(Trail trail1, Trail trail2) {
		double res = 0;		double tmp = 0;
		double min = Integer.MAX_VALUE;		double max = 0;
		for(int i = 0; i < trail1.getPoints().size(); i ++) {
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
	public double calcAngleD(Trail trail1, Trail trail2) {
		double res = 0;		double tmp = 0;
		double min = Integer.MAX_VALUE;		double max = 0;
		for(int i = 0; i < trail1.getPoints().size(); i ++) {
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

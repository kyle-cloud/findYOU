package calculation;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;

import com.mongodb.client.model.ParallelCollectionScanOptions;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import trail.Point;
import trail.Trail;

public class calculations {
	/**
	 * @author kyle_cloud
	 *
	 *�����Ƚ�ά
	 *���룺һ���켣
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
	 *ϸ���Ƚ�ά
	 *���룺һ���켣
	 */
	public ArrayList<Object> fineCompress(ArrayList<Trail> trail, Integer l, Integer lambda) {
		ArrayList<Object> result = new ArrayList<>();
		ArrayList<Trail> finTra = new ArrayList<>(); //���չ켣
		ArrayList<Point> tmpTra = new ArrayList<>(); //�ӹ켣��ά֮��
		ArrayList<Integer> fSum = new ArrayList<>(); //���չ켣ÿ�ε����
		int fsum = 0; //�ӹ켣��ά��ĵ����
		ArrayList<Point> minTra = new ArrayList<Point>();
		Point minp = new Point();
		for(int i = 0; i < trail.size(); i ++) {
			Trail subTra = trail.get(i);
			ArrayList<Point> points = subTra.getPoints();
			minTra.add(subTra.getPoints().get(0)); //�洢ÿһ���ֶ�
			for(int j = 1; j < points.size(); j ++) {
				if(calcDistance(points.get(j), minTra.get(0)) >= l || calcDistOfDate(points.get(j), minTra.get(0)) >= lambda) {
					double sumTime = calcDistOfDate(minTra.get(0), minTra.get(minTra.size()-1));
					//��Ȩ��
					ArrayList<Double> wp = new ArrayList<>();
					ArrayList<Point> qc_points = new ArrayList<>();
					for(int k = 0; k < minTra.size(); k ++) {
						int stay = k;
						for(int h = k; h < minTra.size(); h ++) {//�鿴���ͣ���ڴ˵��ʱ��
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
					//��ۺϵ�
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
	 *�ƶ��켣����ȡ����Ϣ�أ�
	 *���룺һ���켣
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
	 *���ƶȾ���
	 *���룺�����켣
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
	 *ʱ���ֵ���ƶȼ���
	 *���룺�����켣
	 */
	public double innerSimilarity(ArrayList<Trail> topTra, ArrayList<Trail> finTra) {
		double H = 0;
		for(int i = 0; i < topTra.size(); i ++) {
			for(int j = 0; j < finTra.size(); j ++) {
				if(topTra.get(i).getTstart() != finTra.get(j).getTstart()) continue;
				Trail trail1 = new Trail();
				Trail trail2 = new Trail();
				trail1 = topTra.get(i);
				trail2 = finTra.get(j);
				Point pre_trail1 = null;	Point pre_trail2 = null;
				Point nxt_trail1 = null; Point nxt_trail2 = null;
				if(i > 0) pre_trail1 = topTra.get(i-1).getPoints().get(topTra.get(i-1).getPoints().size() - 1);
				if(j > 0) pre_trail2 = finTra.get(j-1).getPoints().get(finTra.get(j-1).getPoints().size() - 1);
				if(i < topTra.size()-1) nxt_trail1 = topTra.get(i+1).getPoints().get(0);
				if(j < finTra.size()-1) nxt_trail2 = finTra.get(j+1).getPoints().get(0);
				insertPoints(trail1.getPoints(), trail2.getPoints(), pre_trail1, pre_trail2, nxt_trail1, nxt_trail2);
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
	 *ʱ���ֵ
	 *���룺�����ӹ켣��
	 */
	public void insertPoints(ArrayList<Point> points1, ArrayList<Point> points2, Point pre_1, Point pre_2, Point nxt_1, Point nxt_2) {
		//��points1�����points2��ֵ
		int i = 0, j = 0;
		for(; i < points1.size(); i ++) {
			if(pre_1 == null) i ++;
			for(; j < points2.size(); j ++) {
				if(points2.get(j).getDate().getTime() < pre_1.getDate().getTime()) { 
					j ++;
				} else if(points2.get(j).getDate().getTime() > points1.get(i).getDate().getTime()) {
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
		
		//��points1�����points2��ֵ
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *�����ӹ켣��Hausdorff
	 */
	public double calcH(ArrayList<Trail> trail1, ArrayList<Trail> trail2) {
		double res = 0;
		for(int i = 0; i < trail1.size(); i ++) {
			res += calcHk(trail1.get(i).getPoints(), trail2.get(i).getPoints());
		}
		return res/trail1.size();
	}
	
	
	/**
	 * @author kyle_cloud
	 *
	 *�����ӹ켣��Hausdorff
	 */
	public double calcHk(ArrayList<Point> trail1, ArrayList<Point> trail2) {
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
	 *����֮��ľ���
	 */
	public double calcDistance(Point first, Point second) {
		double ma_x = first.getLng() - second.getLng();
        double ma_y = first.getLat() - second.getLat();
        return Math.sqrt(ma_x * ma_x + ma_y * ma_y);
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *����ʱ���,����ֵ
	 */
	public double calcDistOfDate(Point p1, Point p2) {
		return Math.abs(p1.getDate().getTime() - p1.getDate().getTime());
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *ƽ��ʱ��
	 */
	public Date meanDate(Date d1, Date d2) {
		Date meanDate = new Date();
		meanDate.setTime((d1.getTime() + d2.getTime()) / 2);
		return meanDate;
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *����ת�Ǵ�С
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
	 *����ĳһ�켣��Ϣ��
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
	 *��ͨ�����
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
	 *���켣���ƶ�
	 */
	public double calcSim(Trail trail1, Trail trail2, double alpha) {
		return 1 - alpha * calcLocD(trail1, trail2) + (1 - alpha) * calcAngleD(trail1, trail2);
	}
	
	/**
	 * @author kyle_cloud
	 *
	 *���켣λ�þ���
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
	 *���켣��״����
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

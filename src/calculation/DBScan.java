package calculation;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import process.downloadData;
import rtree.Constants;
import rtree.RTDataNode;
import rtree.RTNode;
import rtree.RTree;
import rtree.Rectangle;
import trail.Line;
import trail.Point;
import trail.Trail;

import java.util.Iterator;
import java.util.Queue;
 
public class DBScan {
 
    double Eps = 20000;   //区域半径
    int MinPts = 500;   //密度
     
    //由于自己到自己的距离是0,所以自己也是自己的neighbor
    public Vector<Line> getNeighbors(Line p,ArrayList<Line> objects){
        Vector<Line> neighbors = new Vector<Line>();
        Iterator<Line> iter = objects.iterator();
        while(iter.hasNext()){
        	Line q = iter.next();
            ArrayList<Double> result = calculations.calcXianduanDistance(p.getStart_point(), p.getEnd_point(), q.getStart_point(), q.getEnd_point());
            //System.out.println(result.get(0) + result.get(1) + result.get(2));
            if((result.get(0) + result.get(1) + result.get(2)) <= Eps){
                neighbors.add(q);
            }
        }
        return neighbors;
    }
    
    public RTree createRTree(ArrayList<Line> lines) {
    	// 结点容量：4、填充因子：0.4、树类型：二维
        RTree tree = new RTree(4, 0.4f, Constants.RTREE_QUADRATIC, 2);
        for(int i = 0; i < lines.size(); i ++) {
        	//System.out.println(i);
        	final Rectangle rectangle = new Rectangle(lines.get(i));
        	tree.insert(rectangle);
        }
        return tree;
	}
    
    public Vector<Line> getNeighborsByRTree(RTree rTree, Rectangle rectangle) {
		Vector<Line> lines = new Vector<>();
    	RTNode leaf_upper = rTree.root.findLeaf(rectangle).getParent(); //找到上一层
		lines.addAll(rTree.findAllLines(leaf_upper));
		return lines;
	}
     
    public int dbscan(ArrayList<Line> objects){
    	RTree rTree = createRTree(objects);
    	System.out.println(rTree.root.getLevel() + "层R树创建完成");
    	int clusterID = 0;
        boolean AllVisited = false;
        while(!AllVisited){
            Iterator<Line> iter = objects.iterator();
            while(iter.hasNext()){
                Line p = iter.next();
                if(p.isVisited())
                	continue;
                AllVisited = false;
                p.setVisited(true);     //设为visited后就已经确定了它是核心点还是边界点
                Rectangle rectangle = new Rectangle(p);
                Vector<Line> neighbors = getNeighborsByRTree(rTree, rectangle);
                System.out.println(neighbors.size());
                if(neighbors.size() < MinPts){
                    if(p.getCid() <= 0)
                        p.setCid(-1);       //cid初始为0,表示未分类；分类后设置为一个正数；设置为-1表示噪声。
                }else{
                    if(p.getCid() <= 0){
                        clusterID ++;
                        expandCluster(p, neighbors, clusterID, objects);
                    }else{
                        int iid = p.getCid();
                        expandCluster(p, neighbors, iid, objects);
                    }
                }
                AllVisited = true;
            }
        }
        return clusterID;
    }
 
    private void expandCluster(Line p, Vector<Line> neighbors, int clusterID, ArrayList<Line> objects) {
        p.setCid(clusterID);
        Iterator<Line> iter = neighbors.iterator();
        while(iter.hasNext()){
        	Line q = iter.next();
            if(!q.isVisited()){
                q.setVisited(true);
                Vector<Line> qneighbors = getNeighbors(q,objects);
                if(qneighbors.size() >= MinPts){
                    Iterator<Line> it = qneighbors.iterator();
                    while(it.hasNext()){
                    	Line no = it.next();
                        if(no.getCid() <= 0)
                            no.setCid(clusterID);
                    }
                }
            }
            if(q.getCid() <= 0){       //q不是任何簇的成员
                q.setCid(clusterID);
            }
        }
    }
    
	public ArrayList<Line> structCluster(ArrayList<Line> lines, double theta, int Minpts) {
		ArrayList<Line> cores = new ArrayList<>();
		ArrayList<ArrayList<Line>> Ntheta = new ArrayList<>();
		ArrayList<Line> noises = new ArrayList<>();
		for(int i = 0; i < lines.size(); i ++) {
			ArrayList<Line> N_tmp = new ArrayList<>();
			for(int j = 0; j < lines.size(); j ++) {
				if(j == i) continue;
				ArrayList<Double> result = calculations.calcXianduanDistance(lines.get(i).getStart_point(), lines.get(i).getEnd_point(), lines.get(j).getStart_point(), lines.get(j).getEnd_point());
				double sim = result.get(0) + result.get(1) + result.get(2);
				if(sim >= theta && j != i) {
					N_tmp.add(lines.get(j));
				}
			}
			System.out.println(N_tmp.size());
			if(N_tmp.size() >= Minpts) {
				cores.add(lines.get(i));
				Ntheta.add(N_tmp);
			}
		}
		int k = 0;
		for(int i = 0; i < cores.size(); i ++) {
			if(cores.get(i).getCid() != 0) continue;
			k ++;
			cores.get(i).setCid(k);
			connectDensity(cores.get(i), lines, cores, Ntheta, i, k);
		}
		for(int i = 0; i < lines.size(); i ++) {
			if(lines.get(i).getCid() == 0) {
				noises.add(lines.get(i));
			}
		}
		return noises;
	}
	public void connectDensity(Line core, ArrayList<Line> lines, ArrayList<Line> cores, ArrayList<ArrayList<Line>> N_lines, int index, int id) {
		Queue<Line> queue = new LinkedBlockingQueue<>();
		queue.addAll(N_lines.get(index));
		while(!queue.isEmpty()) {
			Line curLine = queue.poll();
			int index_temp = cores.indexOf(curLine);
			if(index_temp == -1) {
				curLine.setCid(id);
			} else if(curLine.getCid() == 0) {
				curLine.setCid(id);
				queue.addAll(N_lines.get(index_temp));
			}
		}
	}
 
    public static void main(String[] args){
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
		System.out.println("开始聚类" + lines.size() + "条线段");
		DBScan dbScan = new DBScan();
		dbScan.dbscan(lines);
		//dbScan.structCluster(lines, 25000, 2000);
//		for(int i = 0; i < lines.size(); i ++) {
//			System.out.println(lines.get(i).getCid());
//		}
    }
}
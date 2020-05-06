package calculation;

import java.util.ArrayList;
import java.util.Vector;

import process.downloadData;
import trail.Line;
import trail.Point;
import trail.Trail;

import java.util.Iterator;
 
public class DBScan {
 
    double Eps = 25000;   //����뾶
    int MinPts = 4;   //�ܶ�
     
    //�����Լ����Լ��ľ�����0,�����Լ�Ҳ���Լ���neighbor
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
     
    public int dbscan(ArrayList<Line> objects){
    	int clusterID = 0;
        boolean AllVisited = false;
        while(!AllVisited){
            Iterator<Line> iter = objects.iterator();
            while(iter.hasNext()){
                Line p = iter.next();
                if(p.isVisited())
                	continue;
                AllVisited = false;
                p.setVisited(true);     //��Ϊvisited����Ѿ�ȷ�������Ǻ��ĵ㻹�Ǳ߽��
                Vector<Line> neighbors = getNeighbors(p, objects);
                System.out.println(neighbors.size());
                if(neighbors.size() < MinPts){
                    if(p.getCid() <= 0)
                        p.setCid(-1);       //cid��ʼΪ0,��ʾδ���ࣻ���������Ϊһ������������Ϊ-1��ʾ������
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
            if(q.getCid() <= 0){       //q�����κδصĳ�Ա
                q.setCid(clusterID);
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
		System.out.println("��ʼ����");
		DBScan dbScan = new DBScan();
		dbScan.dbscan(lines);
    }
}
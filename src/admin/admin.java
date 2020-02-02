package admin;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.synth.SynthViewportUI;
import javax.xml.soap.SAAJResult;

import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

import sun.security.util.Length;
import trail.Point;
import trail.Trail;

public class admin {
	public ArrayList<Point> coarseCompress(ArrayList<Trail> trail, ArrayList<Integer> sum) throws Exception{
		ArrayList<Point> CorTra = null;
		ArrayList<Point> subp = null;
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
			subp.get(i).setDate((subTra.getTstart() + subTra.getTend()) / 2);
		}
		return CorTra;
	}
}
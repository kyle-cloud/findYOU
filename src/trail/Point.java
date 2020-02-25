package trail;

import java.util.Date;

public class Point {
	private double lng;
	private double lat;
	private double cor;
	private Date time;
	
	public Point() {
		
	}
	
	public double getLng() {
		return this.lng;
	}
	
	public double getLat() {
		return this.lat;
	}
	
	public double getCor() {
		return cor;
	}
	
	public Date getDate() {
		return this.time;
	}
	
	public void setLng(double lng) {
		this.lng = (double) Math.round(lng * 100000) / 100000;
	}
	
	public void setLat(double lat) {
		this.lat = (double) Math.round(lat * 100000) / 100000;
	}
	
	public void setCor(double cor) {
		this.cor = (double) Math.round(cor * 100000) / 100000;
	}
	
	public void setDate(Date time) {
		this.time = time;
	}
}

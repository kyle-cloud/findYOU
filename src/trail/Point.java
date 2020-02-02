package trail;

import java.sql.Date;

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
		this.lng = lng;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public void setCor(double cor) {
		this.cor = cor;
	}
	
	public void setDate(Date time) {
		this.time = time;
	}
}

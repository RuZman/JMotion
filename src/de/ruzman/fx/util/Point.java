package de.ruzman.fx.util;



public class Point {	
	private double x;
	private double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Point sub(Point point) {
		return new Point(x-point.x, y-point.y);
	}
	
	public Point div(double factor) {
		return new Point(x/factor, y/factor);
	}
	
	public double getDistanceTo(Point point) {
		double dx = point.getX() - x;
		double dy = point.getY() - y;
		
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public double getAngleTo(Point point) {
		double dx = point.getX() - x;
		double dy = point.getY() - y;
		
        return Math.atan2(dy, dx);
	}

	public Point add(Point point) {
		return new Point(x+point.x, y+point.y);
	}
}

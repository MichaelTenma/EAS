package com.ltiex.EAS;

class City {
	private static long stdId = 0L;
	private int x;
	private int y;
	private long id;
	public City(int x,int y) {
		this.setX(x);
		this.setY(y);
		this.setId(stdId);
		City.stdId++;
	}
	private void setY(int y) {
		this.y = y;
	}
	private void setX(int x) {
		this.x = x;
	}
	
	protected long getId() {
		return id;
	}
	private void setId(long id) {
		this.id = id;
	}
	
	protected int getX() {
		return x;
	}
	protected int getY() {
		return y;
	}
	protected int getLen() {
		return x+y;
	}
	
	public String toString() {
		return this.x+"	"+this.y;
	}
}

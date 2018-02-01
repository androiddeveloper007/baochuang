package com.szbc.model;

public class MyCar {

	/**
	 * 品牌
	 */
	private String brand;
	/**
	 * 系列
	 */
	private String series;
	
	/**
	 * 配置
	 */
	private String configuration;
	
	/**
	 * 里程
	 */
	private String range;

	/**
	 * 购车时间
	 */
	private String buyTime;

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(String buyTime) {
		this.buyTime = buyTime;
	}
}

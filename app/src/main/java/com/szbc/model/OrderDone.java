package com.szbc.model;

/*
* 维护保养记录
* */
public class OrderDone {
	public String busiShopName;
	public String orderTime;
	public String busiShopAddress ;
	public String orderNum ;
	public String maintainSun;
	public String priceSum;
	public String doorServicePrice;

	public String getBusiShopName() {
		return busiShopName;
	}

	public void setBusiShopName(String busiShopName) {
		this.busiShopName = busiShopName;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getBusiShopAddress() {
		return busiShopAddress;
	}

	public void setBusiShopAddress(String busiShopAddress) {
		this.busiShopAddress = busiShopAddress;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getMaintainSun() {
		return maintainSun;
	}

	public void setMaintainSun(String maintainSun) {
		this.maintainSun = maintainSun;
	}

	public String getPriceSum() {
		return priceSum;
	}

	public void setPriceSum(String priceSum) {
		this.priceSum = priceSum;
	}

	public String getDoorServicePrice() {
		return doorServicePrice;
	}

	public void setDoorServicePrice(String doorServicePrice) {
		this.doorServicePrice = doorServicePrice;
	}
}

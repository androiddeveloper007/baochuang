package com.szbc.model;

/*当前正在进行的订单列表*/
public class Order {
	private String id;
	private String orderState;
	private String statusInfo;
	private String orderNum;
	private String busiShopAddress;
	private String maintTime;//保养时间
	private String busiShopPhone;
	private String orderTime;//下单时间
	private String carFetchTime;//取车时间
	private String handleTime;//操作时间
	private String handleId;

	public String getHandleId() {
		return handleId;
	}

	public void setHandleId(String handleId) {
		this.handleId = handleId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getStatusInfo() {
		return statusInfo;
	}

	public void setStatusInfo(String statusInfo) {
		this.statusInfo = statusInfo;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getBusiShopAddress() {
		return busiShopAddress;
	}

	public void setBusiShopAddress(String busiShopAddress) {
		this.busiShopAddress = busiShopAddress;
	}

	public String getMaintTime() {
		return maintTime;
	}

	public void setMaintTime(String maintTime) {
		this.maintTime = maintTime;
	}

	public String getBusiShopPhone() {
		return busiShopPhone;
	}

	public void setBusiShopPhone(String busiShopPhone) {
		this.busiShopPhone = busiShopPhone;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getCarFetchTime() {
		return carFetchTime;
	}

	public void setCarFetchTime(String carFetchTime) {
		this.carFetchTime = carFetchTime;
	}

	public String getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(String handleTime) {
		this.handleTime = handleTime;
	}
}

package com.szbc.front.mine.myorder.model;

/**
 * Created by ZP on 2017/6/27.
 */

public class ServiceInfo {
    private String carNumber;
    private String orderTime;
    private String busiShopName;
    private String orderId;
    private String busiShopAddress;
    private String busiShopPhone;
    private String carRunKm;
    private String maintTime;
    private String priceSum;
    private String orderNum;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getBusiShopName() {
        return busiShopName;
    }

    public void setBusiShopName(String busiShopName) {
        this.busiShopName = busiShopName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBusiShopAddress() {
        return busiShopAddress;
    }

    public void setBusiShopAddress(String busiShopAddress) {
        this.busiShopAddress = busiShopAddress;
    }

    public String getBusiShopPhone() {
        return busiShopPhone;
    }

    public void setBusiShopPhone(String busiShopPhone) {
        this.busiShopPhone = busiShopPhone;
    }

    public String getCarRunKm() {
        return carRunKm;
    }

    public void setCarRunKm(String carRunKm) {
        this.carRunKm = carRunKm;
    }

    public String getMaintTime() {
        return maintTime;
    }

    public void setMaintTime(String maintTime) {
        this.maintTime = maintTime;
    }

    public String getPriceSum() {
        return priceSum;
    }

    public void setPriceSum(String priceSum) {
        this.priceSum = priceSum;
    }
}

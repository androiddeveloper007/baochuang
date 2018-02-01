package com.szbc.model;

import java.util.List;

public class Shop {
	private String linkPhone;
	private String busiShopName;
	private String addressDetail;
	private String busiShopLatitude;
	private String prmyOperBrand;
	private String busiBeginTime;
	private String busiShopPoiId;
	private String id;
	private String linkTel;
	private String busiEndTime;
	private String busiShopLongitude;
	private List<String> pictures;

	public String getLinkPhone() {
		return linkPhone;
	}

	public void setLinkPhone(String linkPhone) {
		this.linkPhone = linkPhone;
	}

	public String getBusiShopName() {
		return busiShopName;
	}

	public void setBusiShopName(String busiShopName) {
		this.busiShopName = busiShopName;
	}

	public String getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}

	public String getBusiShopLatitude() {
		return busiShopLatitude;
	}

	public void setBusiShopLatitude(String busiShopLatitude) {
		this.busiShopLatitude = busiShopLatitude;
	}

	public String getPrmyOperBrand() {
		return prmyOperBrand;
	}

	public void setPrmyOperBrand(String prmyOperBrand) {
		this.prmyOperBrand = prmyOperBrand;
	}

	public String getBusiBeginTime() {
		return busiBeginTime;
	}

	public void setBusiBeginTime(String busiBeginTime) {
		this.busiBeginTime = busiBeginTime;
	}

	public String getBusiShopPoiId() {
		return busiShopPoiId;
	}

	public void setBusiShopPoiId(String busiShopPoiId) {
		this.busiShopPoiId = busiShopPoiId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLinkTel() {
		return linkTel;
	}

	public void setLinkTel(String linkTel) {
		this.linkTel = linkTel;
	}

	public String getBusiEndTime() {
		return busiEndTime;
	}

	public void setBusiEndTime(String busiEndTime) {
		this.busiEndTime = busiEndTime;
	}

	public String getBusiShopLongitude() {
		return busiShopLongitude;
	}

	public void setBusiShopLongitude(String busiShopLongitude) {
		this.busiShopLongitude = busiShopLongitude;
	}

	public List<String> getPictures() {
		return pictures;
	}

	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}
}

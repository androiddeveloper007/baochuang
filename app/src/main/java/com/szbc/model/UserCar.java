package com.szbc.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserCar implements Parcelable {
	public String carYearstyle;
	public String carBrand;
	public String carUseyears;
	public String carinfo;
	public String carBrandId;
	public String carRunKm;
	public String carBrandTypeId;
	public String carYearstyleId;
	public String carUsekind;
	public String carModle;
	public String userId;
	public String carNumber;
	public String carBuyTime;
	public String createTime;
	public String carBrandType;
	public String carModleId;
	public String id;
	public String inputtime;
	public String isDefault;
	public String brandPictureUrl;

	public String getCarYearStyle() {
		return carYearstyle;
	}

	public void setCarYearStyle(String carYearStyle) {
		this.carYearstyle = carYearStyle;
	}

	public String getCarBrand() {
		return carBrand;
	}

	public void setCarBrand(String carBrand) {
		this.carBrand = carBrand;
	}

	public String getCarUseyears() {
		return carUseyears;
	}

	public void setCarUseyears(String carUseyears) {
		this.carUseyears = carUseyears;
	}

	public String getCarinfo() {
		return carinfo;
	}

	public void setCarinfo(String carinfo) {
		this.carinfo = carinfo;
	}

	public String getCarBrandId() {
		return carBrandId;
	}

	public void setCarBrandId(String carBrandId) {
		this.carBrandId = carBrandId;
	}

	public String getCarRunKm() {
		return carRunKm;
	}

	public void setCarRunKm(String carRunKm) {
		this.carRunKm = carRunKm;
	}

	public String getCarBrandTypeId() {
		return carBrandTypeId;
	}

	public void setCarBrandTypeId(String carBrandTypeId) {
		this.carBrandTypeId = carBrandTypeId;
	}

	public String getCarYearstyleId() {
		return carYearstyleId;
	}

	public void setCarYearstyleId(String carYearstyleId) {
		this.carYearstyleId = carYearstyleId;
	}

	public String getCarUsekind() {
		return carUsekind;
	}

	public void setCarUsekind(String carUsekind) {
		this.carUsekind = carUsekind;
	}

	public String getCarModle() {
		return carModle;
	}

	public void setCarModle(String carModle) {
		this.carModle = carModle;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public String getCarBuyTime() {
		return carBuyTime;
	}

	public void setCarBuyTime(String carBuyTime) {
		this.carBuyTime = carBuyTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCarBrandType() {
		return carBrandType;
	}

	public void setCarBrandType(String carBrandType) {
		this.carBrandType = carBrandType;
	}

	public String getCarModleId() {
		return carModleId;
	}

	public void setCarModleId(String carModleId) {
		this.carModleId = carModleId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInputtime() {
		return inputtime;
	}

	public void setInputtime(String inputtime) {
		this.inputtime = inputtime;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getBrandPictureUrl() {
		return brandPictureUrl;
	}

	public void setBrandPictureUrl(String brandPictureUrl) {
		this.brandPictureUrl = brandPictureUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	public static final Parcelable.Creator<UserCar> CREATOR = new Creator<UserCar>() {
		public UserCar createFromParcel(Parcel source) {
			UserCar personPar = new UserCar();
			personPar.carYearstyle = source.readString();
			personPar.carBrand = source.readString();
			personPar.carUseyears = source.readString();
			personPar.carinfo = source.readString();
			personPar.carBrandId = source.readString();
			personPar.carRunKm = source.readString();
			personPar.carBrandTypeId = source.readString();
			personPar.carYearstyleId = source.readString();
			personPar.carUsekind = source.readString();
			personPar.carModle = source.readString();
			personPar.userId = source.readString();
			personPar.carNumber = source.readString();
			personPar.carBuyTime = source.readString();
			personPar.createTime = source.readString();
			personPar.carBrandType = source.readString();
			personPar.carModleId = source.readString();
			personPar.id = source.readString();
			personPar.inputtime = source.readString();
			personPar.isDefault = source.readString();
			personPar.brandPictureUrl = source.readString();
			return personPar;
		}

		public UserCar[] newArray(int size) {
			return new UserCar[size];
		}
	};
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(carYearstyle);
		dest.writeString(carBrand);
		dest.writeString(carUseyears);
		dest.writeString(carinfo);
		dest.writeString(carBrandId);
		dest.writeString(carRunKm);
		dest.writeString(carBrandTypeId);
		dest.writeString(carYearstyleId);
		dest.writeString(carUsekind);
		dest.writeString(carModle);
		dest.writeString(userId);
		dest.writeString(carNumber);
		dest.writeString(carBuyTime);
		dest.writeString(createTime);
		dest.writeString(carBrandType);
		dest.writeString(carModleId);
		dest.writeString(id);
		dest.writeString(inputtime);
		dest.writeString(isDefault);
		dest.writeString(brandPictureUrl);
	}
}

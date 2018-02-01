package com.szbc.model;

public class CarSeries {
	private String modleId;
	private String brandId;
	private String brandTypeId;
	private String brandTypeName;
	private String modleName;

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getModelName() {
		return modleName;
	}

	public void setModelName(String modelName) {
		this.modleName = modelName;
	}

	public String getModleId() {
		return modleId;
	}

	public void setModleId(String modleId) {
		this.modleId = modleId;
	}

	public String getBrandTypeId() {
		return brandTypeId;
	}

	public void setBrandTypeId(String brandTypeId) {
		this.brandTypeId = brandTypeId;
	}

	public String getBrandTypeName() {
		return brandTypeName;
	}

	public void setBrandTypeName(String brandTypeName) {
		this.brandTypeName = brandTypeName;
	}
}

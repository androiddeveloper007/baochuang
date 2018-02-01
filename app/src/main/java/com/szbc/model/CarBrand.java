package com.szbc.model;

public class CarBrand {
	private String brandId;
	private String initialLetter;
	private String brandName;
	private String brandPictureUrl;

	public String getId() {
		return brandId;
	}

	public void setId(String id) {
		this.brandId = id;
	}

	public String getInitialLetter() {
		return initialLetter;
	}

	public void setInitialLetter(String initialLetter) {
		this.initialLetter = initialLetter;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandPictureUrl() {
		return brandPictureUrl;
	}

	public void setBrandPictureUrl(String brandPictureUrl) {
		this.brandPictureUrl = brandPictureUrl;
	}
}

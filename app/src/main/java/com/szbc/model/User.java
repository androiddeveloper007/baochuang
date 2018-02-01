package com.szbc.model;

public class User {

	private String userId;

	private String userName;

	private String userPhone;

	private String userSex;

	private String userPictureUrl;

	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getUserId()
	{
		return this.userId;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserPhone(String phone)
	{
		this.userPhone = phone;
	}
	public String getUserPhone()
	{
		return this.userPhone;
	}
	
	public void setUserSex(String nickName)
	{
		this.userSex = nickName;
	}
	public String getUserSex()
	{
		return this.userSex;
	}
	
	public void setUserPictureUrl(String idNo)
	{
		this.userPictureUrl = idNo;
	}
	public String getUserPictureUrl()
	{
		return this.userPictureUrl;
	}
}

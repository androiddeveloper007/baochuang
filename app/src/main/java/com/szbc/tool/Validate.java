package com.szbc.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {

	static public boolean validateUserName(String userName)
	{
		if(userName!=null)
		{
			Pattern p = null;
			Matcher m = null;
			p = Pattern.compile("^(?!^\\d+$)^[\\u4e00-\\u9fa5a-zA-Z\\d]+$");
			m = p.matcher(userName);
			return m.matches();
		}
		return false;
	}
	
	static public boolean validatePhone(String phone)
	{
		if(phone!=null)
		{
			Pattern p = null;
			Matcher m = null;
			p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
			m = p.matcher(phone);
			return m.matches();
		}
		return false;
	}
	
	static public boolean validatePwd(String pwd)
	{
		if(pwd!=null)
		{
			Pattern p = null;
			Matcher m = null;
			p = Pattern.compile("^(?!([a-zA-Z]+|\\d+)$)[a-zA-Z\\d]{8,20}$");
			m = p.matcher(pwd);
			return m.matches();
		}
		return false;
	}
	
	static public boolean validateSelect(String text)
	{
		if(text.contains("请"))
		{
			return false;
		}
		return true;
	}
	static public boolean validateCardIdNo(String str){
		if(str==null)
		{
			return false;
		}
		
		String regex = "^[0-9]{10,20}$";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(str);
		if(!matcher.matches())
		{
			return false;
		}
		return true;
	}
	static public boolean validateZhihang(String str){
		if(str==null)
		{
			return false;
		}
		
		String regex = "^[\u4e00-\u9fa5]{5,20}$";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(str);
		if(!matcher.matches())
		{
			return false;
		}
		return true;
	}
	static public boolean validateInvestMoney(String str,String str2){
		if(str == null || str2 == null){
			return false;
		}
		String regex = "^[1-9]\\d*"+str2.substring(1, str2.length())+"$";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(str);
		if(!matcher.matches())
		{
			return false;
		}
		return true;
	}
	static public boolean validateMoney(String str)
	{
		if(str==null)
		{
			return false;
		}
		
		String regex = "^[0-9]+$|^[0-9]+\\.[0-9]{1,2}$";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(str);
		if(!matcher.matches())
		{
			return false;
		}
		return true;
	}
	static public boolean validataRealName(String realName){
		if(realName!=null)
		{
			realName=new String(realName.getBytes());
			Pattern p = null;
			Matcher m = null;
			p = Pattern.compile("^([\u4e00-\u9fa5]{2,6})$");
			m = p.matcher(realName);
			return m.find();
		}
		return false;
	}
}

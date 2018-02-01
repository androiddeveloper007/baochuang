package com.szbc.front.addcar.sortedlistview;

import com.szbc.widget.dropdonwdemo.model.addressBean;
import com.szbc.tool.AppCollector;

import java.util.Comparator;

/**
 * 
 * @author 
 *
 */
public class PinyinComparatorCity implements Comparator<addressBean.city> {

	public int compare(addressBean.city o1, addressBean.city o2) {
		if (AppCollector.getPinYin(o1.getCityName()).substring(0,1).equals("@")
				|| AppCollector.getPinYin(o2.getCityName()).substring(0,1).equals("#")) {
			return -1;
		} else if (AppCollector.getPinYin(o1.getCityName()).substring(0,1).equals("#")
				|| AppCollector.getPinYin(o2.getCityName()).substring(0,1).equals("@")) {
			return 1;
		} else {
			return AppCollector.getPinYin(o1.getCityName()).substring(0,1)
					.compareTo(AppCollector.getPinYin(o2.getCityName()).substring(0,1));
		}
	}
}

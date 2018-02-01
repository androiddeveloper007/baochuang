package com.szbc.front.addcar.sortedlistview;

import com.szbc.model.CarBrand;

import java.util.Comparator;

/**
 * 
 * @author 
 *
 */
public class PinyinComparator1 implements Comparator<CarBrand> {

	public int compare(CarBrand o1, CarBrand o2) {
		if (o1.getInitialLetter().equals("@")
				|| o2.getInitialLetter().equals("#")) {
			return -1;
		} else if (o1.getInitialLetter().equals("#")
				|| o2.getInitialLetter().equals("@")) {
			return 1;
		} else {
			return o1.getInitialLetter().compareTo(o2.getInitialLetter());
		}
	}

}

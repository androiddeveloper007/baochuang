package com.szbc.widget.dropdonwdemo.utils;


import android.content.Context;
import android.text.TextUtils;

import com.szbc.widget.dropdonwdemo.model.ChannelEntity;
import com.szbc.widget.dropdonwdemo.model.FilterEntity;
import com.szbc.widget.dropdonwdemo.model.FilterTwoEntity;
import com.szbc.widget.dropdonwdemo.model.OperationEntity;
import com.szbc.widget.dropdonwdemo.model.TravelingEntity;
import com.szbc.widget.dropdonwdemo.model.TravelingEntityComparator;
import com.szbc.android.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 好吧，让你找到了，这是假的数据源
 *
 * Created by sunfusheng on 16/4/22.
 */
public class ModelUtil {

    public static final String type_scenery = "北京";
    public static final String type_building = "上海";
    public static final String type_animal = "广州";
    public static final String type_plant = "深圳";

    // 省份list
    public static List<String> getProvinceData(Context context) {
        List<String> list = new ArrayList<>();
        list = Arrays.asList(context.getResources().getStringArray(R.array.province_item));
        return list;
    }

    // 城市list
    public static List<String> getCityData() {
        List<String> adList = new ArrayList<>();
        adList.add("http://img0.imgtn.bdimg.com/it/u=1270781761,1881354959&fm=21&gp=0.jpg");
        adList.add("http://img0.imgtn.bdimg.com/it/u=2138116966,3662367390&fm=21&gp=0.jpg");
        adList.add("http://img0.imgtn.bdimg.com/it/u=1296117362,655885600&fm=21&gp=0.jpg");
        return adList;
    }

    // 频道数据
    public static List<ChannelEntity> getChannelData() {
        List<ChannelEntity> channelList = new ArrayList<>();
        channelList.add(new ChannelEntity("中国", "天安门", "http://img2.imgtn.bdimg.com/it/u=2850936076,2080165544&fm=206&gp=0.jpg"));
        channelList.add(new ChannelEntity("美国", "白宫", "http://img3.imgtn.bdimg.com/it/u=524208507,12616758&fm=206&gp=0.jpg"));
        channelList.add(new ChannelEntity("英国", "伦敦塔桥", "http://img3.imgtn.bdimg.com/it/u=698582197,4250615262&fm=206&gp=0.jpg"));
        channelList.add(new ChannelEntity("德国", "城堡", "http://img5.imgtn.bdimg.com/it/u=1467751238,3257336851&fm=11&gp=0.jpg"));
        channelList.add(new ChannelEntity("西班牙", "巴塞罗那", "http://img5.imgtn.bdimg.com/it/u=3191365283,111438732&fm=21&gp=0.jpg"));
        channelList.add(new ChannelEntity("意大利", "比萨斜塔", "http://img5.imgtn.bdimg.com/it/u=482494496,1350922497&fm=206&gp=0.jpg"));
        return channelList;
    }

    // 运营数据
    public static List<OperationEntity> getOperationData() {
        List<OperationEntity> operationList = new ArrayList<>();
        operationList.add(new OperationEntity("度假游", "度假的天堂", "http://img2.imgtn.bdimg.com/it/u=4081165325,36916497&fm=21&gp=0.jpg"));
        operationList.add(new OperationEntity("蜜月游", "浪漫的港湾", "http://img4.imgtn.bdimg.com/it/u=4141168524,78676102&fm=21&gp=0.jpg"));
        return operationList;
    }

    // ListView数据
    public static List<TravelingEntity> getTravelingData() {
        List<TravelingEntity> travelingList = new ArrayList<>();
        travelingList.add(new TravelingEntity(type_scenery, "大理", "奔驰", 1, ""+R.drawable.lv_item_icon0));
        travelingList.add(new TravelingEntity(type_scenery, "", "宝马", 2, "android.resource://com.frank.glide/drawable/lv_item_icon1"));
        travelingList.add(new TravelingEntity(type_scenery, "", "奥迪", 3, "android.resource://com.frank.glide/drawable/lv_item_icon2"));
        travelingList.add(new TravelingEntity(type_scenery, "拱门", "捷豹", 4, "android.resource://com.frank.glide/drawable/lv_item_icon3"));
        travelingList.add(new TravelingEntity(type_plant, "荷花", "路虎", 5, "android.resource://com.frank.glide/drawable/lv_item_icon0"));
        travelingList.add(new TravelingEntity(type_building, "", "法拉利", 6, "android.resource://com.frank.glide/drawable/lv_item_icon1"));
        travelingList.add(new TravelingEntity(type_scenery, "", "劳斯莱斯", 7, "android.resource://com.frank.glide/drawable/lv_item_icon2"));
        travelingList.add(new TravelingEntity(type_animal, "水貂", "奔驰", 8, "android.resource://com.frank.glide/drawable/lv_item_icon3"));
        travelingList.add(new TravelingEntity(type_plant, "仙人掌", "宝马", 9, "android.resource://com.frank.glide/drawable/lv_item_icon0"));
        travelingList.add(new TravelingEntity(type_scenery, "威尔士", "奥迪", 10, "android.resource://com.frank.glide/drawable/lv_item_icon1"));
        travelingList.add(new TravelingEntity(type_building, "伦敦塔桥", "捷豹", 11, "android.resource://com.frank.glide/drawable/lv_item_icon2"));
        /*travelingList.add(new TravelingEntity(type_animal, "", "路虎", 12, "android.resource://com.frank.glide/drawable/lv_item_icon3"));
        travelingList.add(new TravelingEntity(type_plant, "", "法拉利", 13, "android.resource://com.frank.glide/drawable/lv_item_icon0"));
        travelingList.add(new TravelingEntity(type_scenery, "", "劳斯莱斯", 14, "android.resource://com.frank.glide/drawable/lv_item_icon1"));
        travelingList.add(new TravelingEntity(type_building, "自由女神像", "奔驰", 15, "android.resource://com.frank.glide/drawable/lv_item_icon2"));*/
//        travelingList.add(new TravelingEntity(type_building, "拉萨", "宝马", 16, "android.resource://com.frank.glide/drawable/lv_item_icon3"));
//        travelingList.add(new TravelingEntity(type_animal, "熊猫", "奥迪", 17, "android.resource://com.frank.glide/drawable/lv_item_icon0"));
//        travelingList.add(new TravelingEntity(type_building, "", "捷豹", 18, "android.resource://com.frank.glide/drawable/lv_item_icon1"));
        /*travelingList.add(new TravelingEntity(type_animal, "狗熊", "路虎", 19, "android.resource://com.frank.glide/drawable/lv_item_icon2"));
        travelingList.add(new TravelingEntity(type_plant, "", "法拉利", 20, "android.resource://com.frank.glide/drawable/lv_item_icon3"));
        travelingList.add(new TravelingEntity(type_scenery, "", "劳斯莱斯", 21, "android.resource://com.frank.glide/drawable/lv_item_icon0"));*/
//        travelingList.add(new TravelingEntity(type_building, "", "奔驰", 22, "android.resource://com.frank.glide/drawable/lv_item_icon1"));
        return travelingList;
    }

    // 分类数据
    public static List<FilterTwoEntity> getCategoryData() {
        List<FilterTwoEntity> list = new ArrayList<>();
//        list.add(new FilterTwoEntity(type_scenery, getFilterDataBj()));
//        list.add(new FilterTwoEntity(type_building, getFilterDataSh()));
//        list.add(new FilterTwoEntity(type_animal, getFilterDataGz()));
//        list.add(new FilterTwoEntity(type_plant, getFilterDataSz()));
        return list;
    }

    // 排序数据
    public static List<FilterEntity> getSortData() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("排序从高到低", "1"));
        list.add(new FilterEntity("排序从低到高", "2"));
        return list;
    }

    // 筛选数据
    public static List<FilterEntity> getFilterData() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("不限", ""));
//        list.add(new FilterEntity("奔驰", "2"));
//        list.add(new FilterEntity("宝马", "3"));
//        list.add(new FilterEntity("奥迪", "4"));
//        list.add(new FilterEntity("捷豹", "5"));
//        list.add(new FilterEntity("路虎", "6"));
//        list.add(new FilterEntity("法拉利", "7"));
//        list.add(new FilterEntity("劳斯莱斯", "8"));
        return list;
    }

    // 筛选数据
    public static List<FilterEntity> getFilterDataSz() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("福田", "1"));
        list.add(new FilterEntity("南山", "2"));
        list.add(new FilterEntity("罗湖", "3"));
        list.add(new FilterEntity("盐田", "4"));
        list.add(new FilterEntity("宝安", "5"));
        list.add(new FilterEntity("龙岗", "6"));
        list.add(new FilterEntity("龙华", "7"));
        list.add(new FilterEntity("布吉", "8"));
        list.add(new FilterEntity("坪山新区", "9"));
        return list;
    }

    public static List<FilterEntity> getFilterDataBj() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("朝阳", "1"));
        list.add(new FilterEntity("海淀", "2"));
        list.add(new FilterEntity("东城", "3"));
        list.add(new FilterEntity("西城", "4"));
        list.add(new FilterEntity("崇文", "5"));
        list.add(new FilterEntity("宣武", "6"));
        list.add(new FilterEntity("丰台", "7"));
        list.add(new FilterEntity("通州", "8"));
        list.add(new FilterEntity("石景山", "9"));
        list.add(new FilterEntity("房山", "10"));
        list.add(new FilterEntity("昌平", "11"));
        list.add(new FilterEntity("大兴", "12"));
        return list;
    }

    public static List<FilterEntity> getFilterDataSh() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("黄埔", "1"));
        list.add(new FilterEntity("卢湾", "2"));
        list.add(new FilterEntity("静安", "3"));
        list.add(new FilterEntity("徐汇", "4"));
        list.add(new FilterEntity("浦东", "5"));
        list.add(new FilterEntity("长宁", "6"));
        list.add(new FilterEntity("虹口", "7"));
        list.add(new FilterEntity("杨浦", "8"));
        list.add(new FilterEntity("普陀", "9"));
        list.add(new FilterEntity("闸北", "10"));
        return list;
    }

    public static List<FilterEntity> getFilterDataGz() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("天河", "1"));
        list.add(new FilterEntity("海秀", "2"));
        list.add(new FilterEntity("越秀", "3"));
        list.add(new FilterEntity("横沥", "4"));
        list.add(new FilterEntity("白云", "5"));
        list.add(new FilterEntity("荔湾", "6"));
        list.add(new FilterEntity("番禺", "7"));
        list.add(new FilterEntity("黄埔", "8"));
        return list;
    }
    // ListView分类数据
    public static List<TravelingEntity> getCategoryTravelingData(FilterTwoEntity entity) {
        List<TravelingEntity> list = getTravelingData();
        List<TravelingEntity> travelingList = new ArrayList<>();
        int size = list.size();
        for (int i=0; i<size; i++) {
            if (list.get(i).getType().equals(entity.getType()) &&
                    list.get(i).getFrom().equals(entity.getSelectedFilterEntity().getKey())) {
                travelingList.add(list.get(i));
            }
        }
        return travelingList;
    }

    // ListView排序数据
    public static List<TravelingEntity> getSortTravelingData(FilterEntity entity) {
        List<TravelingEntity> list = getTravelingData();
        Comparator<TravelingEntity> ascComparator = new TravelingEntityComparator();
        if (entity.getKey().equals("排序从高到低")) {
            Collections.sort(list);
        } else {
            Collections.sort(list, ascComparator);
        }
        return list;
    }

    // ListView筛选数据
    public static List<TravelingEntity> getFilterTravelingData(FilterEntity entity) {
        List<TravelingEntity> list = getTravelingData();
        List<TravelingEntity> travelingList = new ArrayList<>();
        int size = list.size();
        if(TextUtils.equals(entity.getValue(),"1")){
            return list;
        }
        for (int i=0; i<size; i++) {
            if (list.get(i).getFrom().equals(entity.getKey())) {
                travelingList.add(list.get(i));
            }
        }
        return travelingList;
    }

    // 暂无数据
    public static List<TravelingEntity> getNoDataEntity(int height) {
        List<TravelingEntity> list = new ArrayList<>();
        TravelingEntity entity = new TravelingEntity();
        entity.setNoData(true);
        entity.setHeight(height);
        list.add(entity);
        return list;
    }

}

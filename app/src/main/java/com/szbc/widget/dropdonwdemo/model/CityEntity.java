package com.szbc.widget.dropdonwdemo.model;

import java.io.Serializable;

/**
 * Created by sunfusheng on 16/4/20.
 */
public class CityEntity implements Serializable
{

    private String province;//省
    private String city;//市

    public CityEntity() {
    }

    public CityEntity(String title, String tips, String image_url) {
        this.province = title;
        this.city = tips;
    }

    public String getTitle() {
        return province;
    }

    public void setTitle(String title) {
        this.province = title;
    }

    public String getTips() {
        return city;
    }

    public void setTips(String tips) {
        this.city = tips;
    }

}

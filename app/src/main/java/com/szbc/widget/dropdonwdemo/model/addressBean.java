package com.szbc.widget.dropdonwdemo.model;

import java.util.List;

/**
 * Created by ZP on 2017/6/20.
 */

public class addressBean {
    private String provinceId;
    private String provinceName;
    private List<city> city;
    public String getProvinceId() {
        return provinceId;
    }
    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }
    public String getProvinceName() {
        return provinceName;
    }
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
    public List<addressBean.city> getCity() {
        return city;
    }
    public void setCity(List<addressBean.city> city) {
        this.city = city;
    }

    public static class city{
        private String cityId;
        private String cityName;
        private List<area> area;
        public List<addressBean.area> getArea() {
            return area;
        }
        public void setArea(List<addressBean.area> area) {
            this.area = area;
        }
        public String getCityId() {
            return cityId;
        }
        public void setCityId(String cityId) {
            this.cityId = cityId;
        }
        public String getCityName() {
            return cityName;
        }
        public void setCityName(String cityName) {
            this.cityName = cityName;
        }
    }

    public static class area{
        private String areaId;
        private String areaName;
        public String getAreaId() {
            return areaId;
        }
        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }
        public String getAreaName() {
            return areaName;
        }
        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }
    }
}

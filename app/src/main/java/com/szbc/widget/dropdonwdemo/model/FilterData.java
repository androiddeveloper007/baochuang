package com.szbc.widget.dropdonwdemo.model;

import java.io.Serializable;
import java.util.List;

public class FilterData implements Serializable {
    private List<FilterThreeEntity> address;
    private List<FilterEntity> brands;

    public List<FilterThreeEntity> getAddress() {
        return address;
    }

    public void setAddress(List<FilterThreeEntity> address) {
        this.address = address;
    }

    public List<FilterEntity> getFilters() {
        return brands;
    }

    public void setBrands(List<FilterEntity> brands) {
        this.brands = brands;
    }
}
package com.szbc.widget.dropdonwdemo.model;

import java.io.Serializable;
import java.util.List;

public class FilterThreeEntity implements Serializable
{

    private String type;
    private String id;
    private List<FilterTwoEntity> list;
    private boolean isSelected;
    private FilterTwoEntity selectedFilterEntity;

    public FilterThreeEntity(String type, String id, List<FilterTwoEntity> list) {
        this.type = type;
        this.id = id;
        this.list = list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FilterTwoEntity getSelectedFilterEntity() {
        return selectedFilterEntity;
    }

    public void setSelectedFilterEntity(FilterTwoEntity selectedFilterEntity) {
        this.selectedFilterEntity = selectedFilterEntity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<FilterTwoEntity> getList() {
        return list;
    }

    public void setList(List<FilterTwoEntity> list) {
        this.list = list;
    }
}

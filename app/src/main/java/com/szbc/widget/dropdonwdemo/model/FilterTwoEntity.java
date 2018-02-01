package com.szbc.widget.dropdonwdemo.model;

import java.io.Serializable;
import java.util.List;

public class FilterTwoEntity implements Serializable
{

    private String type;
    private String id;
    private List<FilterEntity> list;
    private boolean isSelected;
    private FilterEntity selectedFilterEntity;

    public FilterTwoEntity(String type, String id, List<FilterEntity> list) {
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

    public FilterEntity getSelectedFilterEntity() {
        return selectedFilterEntity;
    }

    public void setSelectedFilterEntity(FilterEntity selectedFilterEntity) {
        this.selectedFilterEntity = selectedFilterEntity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<FilterEntity> getList() {
        return list;
    }

    public void setList(List<FilterEntity> list) {
        this.list = list;
    }
}

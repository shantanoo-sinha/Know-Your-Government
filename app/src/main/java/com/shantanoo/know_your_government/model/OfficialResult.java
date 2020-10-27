package com.shantanoo.know_your_government.model;

import java.util.List;

/**
 * Created by Shantanoo on 10/26/2020.
 */
public class OfficialResult {
    private String location;
    private List<Official> officialList;

    public OfficialResult() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Official> getOfficialList() {
        return officialList;
    }

    public void setOfficialList(List<Official> officialList) {
        this.officialList = officialList;
    }

    @Override
    public String toString() {
        return "OfficialResult{" +
                "location='" + location + '\'' +
                ", officialList=" + officialList +
                '}';
    }
}

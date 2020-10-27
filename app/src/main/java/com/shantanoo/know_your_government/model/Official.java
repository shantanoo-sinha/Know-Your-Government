package com.shantanoo.know_your_government.model;

import java.io.Serializable;

/**
 * Created by Shantanoo on 10/25/2020.
 */
public class Official implements Serializable {

    // Official personal info
    private String office;
    private String officialName;
    private String officialParty;
    private String officialAddress;
    private String officialPhone;
    private String officialEmail;
    private String officialPhotoURL;

    // Official social media
    private String officialWebURL;
    private String officialYouTube;
    private String officialFB;
    private String officialTwitter;
    private String officialGooglePlus;

    public Official() {
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getOfficialParty() {
        return officialParty;
    }

    public void setOfficialParty(String officialParty) {
        this.officialParty = officialParty;
    }

    public String getOfficialAddress() {
        return officialAddress;
    }

    public void setOfficialAddress(String officialAddress) {
        this.officialAddress = officialAddress;
    }

    public String getOfficialPhone() {
        return officialPhone;
    }

    public void setOfficialPhone(String officialPhone) {
        this.officialPhone = officialPhone;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public void setOfficialEmail(String officialEmail) {
        this.officialEmail = officialEmail;
    }

    public String getOfficialPhotoURL() {
        return officialPhotoURL;
    }

    public void setOfficialPhotoURL(String officialPhotoURL) {
        this.officialPhotoURL = officialPhotoURL;
    }

    public String getOfficialWebURL() {
        return officialWebURL;
    }

    public void setOfficialWebURL(String officialWebURL) {
        this.officialWebURL = officialWebURL;
    }

    public String getOfficialYouTube() {
        return officialYouTube;
    }

    public void setOfficialYouTube(String officialYouTube) {
        this.officialYouTube = officialYouTube;
    }

    public String getOfficialFB() {
        return officialFB;
    }

    public void setOfficialFB(String officialFB) {
        this.officialFB = officialFB;
    }

    public String getOfficialTwitter() {
        return officialTwitter;
    }

    public void setOfficialTwitter(String officialTwitter) {
        this.officialTwitter = officialTwitter;
    }

    public String getOfficialGooglePlus() {
        return officialGooglePlus;
    }

    public void setOfficialGooglePlus(String officialGooglePlus) {
        this.officialGooglePlus = officialGooglePlus;
    }
}

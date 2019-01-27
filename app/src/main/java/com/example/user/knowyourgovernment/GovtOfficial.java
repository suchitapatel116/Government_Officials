package com.example.user.knowyourgovernment;

import java.io.Serializable;

/**
 * Created by user on 01-04-2018.
 */

public class GovtOfficial implements Serializable {

    private String loc_city;
    private String loc_state;
    private String loc_zip;

    private String office;
    private String official_name;
    private String party;

    private String address;
    private String phone_no;
    private String website_url;
    private String email_id;
    private String photoUrl;

    private String channel_googlePlus_id;
    private String channel_facebook_id;
    private String channel_twitter_id;
    private String channel_youtube_id;

    public String getLoc_city() {
        return loc_city;
    }

    public void setLoc_city(String loc_city) {
        this.loc_city = loc_city;
    }

    public String getLoc_state() {
        return loc_state;
    }

    public void setLoc_state(String loc_state) {
        this.loc_state = loc_state;
    }

    public String getLoc_zip() {
        return loc_zip;
    }

    public void setLoc_zip(String loc_zip) {
        this.loc_zip = loc_zip;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getOfficial_name() {
        return official_name;
    }

    public void setOfficial_name(String official_name) {
        this.official_name = official_name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public void setWebsite_url(String website_url) {
        this.website_url = website_url;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getChannel_googlePlus_id() {
        return channel_googlePlus_id;
    }

    public void setChannel_googlePlus_id(String channel_googlePlus_id) {
        this.channel_googlePlus_id = channel_googlePlus_id;
    }

    public String getChannel_facebook_id() {
        return channel_facebook_id;
    }

    public void setChannel_facebook_id(String channel_facebook_id) {
        this.channel_facebook_id = channel_facebook_id;
    }

    public String getChannel_twitter_id() {
        return channel_twitter_id;
    }

    public void setChannel_twitter_id(String channel_twitter_id) {
        this.channel_twitter_id = channel_twitter_id;
    }

    public String getChannel_youtube_id() {
        return channel_youtube_id;
    }

    public void setChannel_youtube_id(String channel_youtube_id) {
        this.channel_youtube_id = channel_youtube_id;
    }

    @Override
    public String toString() {
        return "GovtOfficial{" +
                "loc_city='" + loc_city + '\'' +
                ", loc_state='" + loc_state + '\'' +
                ", loc_zip='" + loc_zip + '\'' +
                ", office='" + office + '\'' +
                ", official_name='" + official_name + '\'' +
                ", party='" + party + '\'' +
                ", address='" + address + '\'' +
                ", phone_no='" + phone_no + '\'' +
                ", website_url='" + website_url + '\'' +
                ", email_id='" + email_id + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", channel_googlePlus_id='" + channel_googlePlus_id + '\'' +
                ", channel_facebook_id='" + channel_facebook_id + '\'' +
                ", channel_twitter_id='" + channel_twitter_id + '\'' +
                ", channel_youtube_id='" + channel_youtube_id + '\'' +
                '}';
    }
}

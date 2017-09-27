package com.learningbycoding.pub.integration.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GithubUser implements Serializable{
    private String email;
    private String name;
    private String company;
    private String blog;
    private String avatar;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getAvatar() {
        return avatar;
    }

    @JsonProperty("avatar_url")
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

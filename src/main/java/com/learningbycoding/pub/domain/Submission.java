package com.learningbycoding.pub.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@SuppressWarnings("serial")
public class Submission implements Serializable{
    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "developer_id")
    private User developer;

    private String name;
    private String githubrepo;
    private String liveurl;
    private String description;
    private String thumbnail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGithubrepo() {
        return githubrepo;
    }

    public void setGithubrepo(String githubrepo) {
        this.githubrepo = githubrepo;
    }

    public String getLiveurl() {
        return liveurl;
    }

    public void setLiveurl(String liveurl) {
        this.liveurl = liveurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public User getDeveloper() {
        return developer;
    }

    public void setDeveloper(User developer) {
        this.developer = developer;
    }
}

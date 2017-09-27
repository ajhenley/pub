package com.learningbycoding.pub.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@SuppressWarnings("serial")
@Table( name = "Userdata")
public class User implements Serializable{

    @GeneratedValue
    @Id
    private Long id;
    private String email;
    private String name;
    private String twitter;
    private String github;
    private String avatarUrl;

    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL)
    private Set<Submission> submissions;

    @Column
    @Lob
    private String bio;

    public User(){

    }

    public User(String email, String name){
        this.email = email;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Submission> submissions) {
        this.submissions = submissions;
    }
}

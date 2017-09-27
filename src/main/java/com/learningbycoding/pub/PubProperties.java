package com.learningbycoding.pub;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties("pub")
public class PubProperties {
    private final Github github = new Github();
    private final Security security = new Security();
    public Github getGithub(){
        return this.github;
    }
    public Security getSecurity(){
        return this.security;
    }
    public static class Github{
        private String token;
        private String getToken(){
            return this.token;
        }
        public void setToken(String token){
            this.token = token;
        }
    }
    public static class Security {
        private List<String> admins = Arrays.asList("ajhenley", "aoa4eva", "dave45678");
        public List<String> getAdmins(){
            return admins;
        }
        public void setAdmins(List<String> admins){
            this.admins = admins;
        }
    }
}

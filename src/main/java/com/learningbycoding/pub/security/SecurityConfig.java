package com.learningbycoding.pub.security;

import com.learningbycoding.pub.PubProperties;
import com.learningbycoding.pub.domain.User;
import com.learningbycoding.pub.domain.UserRepository;
import com.learningbycoding.pub.integration.github.GithubClient;
import com.learningbycoding.pub.integration.github.GithubUser;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private PubProperties pubProperties;

    public SecurityConfig(PubProperties pubProperties){
        this.pubProperties = pubProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/","/news", "/login**","/css/**","/img/**", "/webjars/**", "/bootstrap/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .csrf()
                .ignoringAntMatchers("/admin/h2-console/*")
                .and()
            .logout()
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
            .headers()
                .frameOptions().sameOrigin();
    }

    @Bean
    public AuthoritiesExtractor authoritiesExtractor(){
        return map -> {
            String username = (String) map.get("login");
            if(this.pubProperties.getSecurity().getAdmins().contains(username)){
                return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_ADMIN");
            } else {
                return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
            }
        };
    }

    @Bean
    public PrincipalExtractor principalExtractor(GithubClient githubClient, UserRepository userRepository){
        return map -> {
            String githubLogin = (String) map.get("login");
            User developer = userRepository.findByGithub(githubLogin);
            if (developer == null){
                GithubUser user = githubClient.getUser(githubLogin);
                developer = new User();
                developer.setEmail(user.getEmail());
                developer.setName(user.getName());
                developer.setGithub(githubLogin);
                developer.setAvatarUrl(user.getAvatar());
                userRepository.save(developer);
            }
            return developer;
        };
    }
}

package com.learningbycoding.pub.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByGithub(String github);
    Collection<User> findByName(String lastName);
}

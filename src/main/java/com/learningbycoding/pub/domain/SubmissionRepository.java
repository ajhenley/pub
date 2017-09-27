package com.learningbycoding.pub.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubmissionRepository extends CrudRepository<Submission, Long> {
    List<Submission> findByDeveloper(User user);
}

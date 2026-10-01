package com.liys.oaback.service;

import com.liys.oaback.common.PageResult;
import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Job;

import java.util.List;

public interface JobService {


    PageResult<Job> page(CommonQuery q);

    List<Job> list();

    void add(Job j);

    void update(Job j);

    void delete(Long id);


}

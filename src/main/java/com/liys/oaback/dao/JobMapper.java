package com.liys.oaback.dao;

import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Dept;
import com.liys.oaback.domain.Job;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@Mapper
public interface JobMapper {

/**
 * Inserts a new job into the system
 *
 * @param job The job object to be inserted into the system
 * This method is responsible for adding a new job to the job management system
 * It takes a Job object as parameter and inserts it into the appropriate data structure
 */
    void insert (Job job); // Method signature for inserting a job
/**
 * Deletes an element with the specified ID.
 *
 * @param id The unique identifier of the element to be deleted
 */
    int deleteById (long id);

    Job selectById(Long id);

    int update (Job job);

    long countEmpByJobId (Long jobId);


    List<Job> selectPage(CommonQuery query);

    long countByQuery(CommonQuery query);

    List<Job> selectAll();
}

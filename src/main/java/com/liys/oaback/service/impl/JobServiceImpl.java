package com.liys.oaback.service.impl;

import com.liys.oaback.common.BusinessException;
import com.liys.oaback.common.PageResult;
import com.liys.oaback.dao.JobMapper;
import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Job;
import com.liys.oaback.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobMapper jobMapper;

    @Override
    public PageResult<Job> page(CommonQuery q) {
        List<Job> records = jobMapper.selectPage(q);
        long total = jobMapper.countByQuery(q);
        return new PageResult<>(records, total, q.getPage(), q.getPageSize());
    }

    @Override
    public List<Job> list() {
        return jobMapper.selectAll();
    }

    @Override
    public void add(Job j) {
        if (j == null) throw new BusinessException("职位信息不能为空");
        validate(j);
        if (j.getSort() == null) j.setSort(0);
        jobMapper.insert(j);
    }

    @Override
    public void update(Job j) {
        if (j == null || j.getId() == null) throw new BusinessException("职位ID不能为空");
        validate(j);
        if (j.getName() == null && j.getSort() == null) throw new BusinessException("请至少提供一个要修改的字段");
        if (jobMapper.update(j) == 0 && jobMapper.selectById(j.getId()) == null) {
            throw new BusinessException("职位不存在");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) throw new BusinessException("职位ID不能为空");
        if (jobMapper.countEmpByJobId(id) > 0) throw new BusinessException("该职位下存在员工，禁止删除");
        if (jobMapper.deleteById(id) == 0) throw new BusinessException("职位不存在");

    }

    private void validate(Job job) {
        if (job.getName() != null) {
            if (job.getName().isBlank()) throw new BusinessException("职位名称不能为空");
            if (job.getName().length() > 50) throw new BusinessException("职位名称不能超过50个字符");
        }
        if (job.getSort() != null && (job.getSort() < 0 || job.getSort() > 999)) {
            throw new BusinessException("职位排序值必须在0到999之间");
        }
    }
}

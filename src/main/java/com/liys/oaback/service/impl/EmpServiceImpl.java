package com.liys.oaback.service.impl;

import com.liys.oaback.common.BusinessException;
import com.liys.oaback.common.PageResult;
import com.liys.oaback.dao.EmpMapper;
import com.liys.oaback.dao.DeptMapper;
import com.liys.oaback.dao.JobMapper;
import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Emp;
import com.liys.oaback.domain.EmpQuery;
import com.liys.oaback.service.EmpService;
import com.liys.oaback.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class EmpServiceImpl  implements EmpService {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private JobMapper jobMapper;
    @Override
    public PageResult<Emp> page(EmpQuery q) {
         // 查询所有员工
        List<Emp> records = empMapper.selectPage(q);
        // 查询总记录数
        long total = empMapper.countByQuery(q);
        // 返回分页结果
        return new PageResult<>(records, total, q.getPage(), q.getPageSize());
    }

    // 查询单个员工
    @Override
    public Emp get(Long id) {
        Emp emp = empMapper.selectById(id);
        if (emp == null) {
            throw new BusinessException("员工不存在");
        }
        return emp;
    }

    @Override
    @Transactional
    public Emp add(Emp e) {
        if (e == null) throw new BusinessException("员工信息不能为空");
        validateName(e.getName());
        validatePhone(e.getPhone(), true);
        validateEmail(e.getEmail());
        validateGender(e.getGender());
        validateDepartmentAndJob(e.getDeptId(), e.getJobId());
        if (e.getHireDate() == null) throw new BusinessException("入职时间不能为空");
        if (e.getGender() == null) e.setGender(1);
        String temporaryCode = "TMP" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        e.setEmpNo(temporaryCode);
        e.setUsername(e.getEmpNo());
        e.setPassword(Md5Util.md5("123456"));
        e.setRole("employee");
        e.setStatus(1);
        e.setAvatar("");
        empMapper.insert(e);
        String generatedCode = String.format(Locale.ROOT, "EMP%03d", e.getId());
        if (empMapper.updateIdentity(e.getId(), generatedCode, generatedCode) == 0) {
            throw new BusinessException("员工账号生成失败");
        }
        e.setEmpNo(generatedCode);
        e.setUsername(generatedCode);
        return e;
    }

    @Override
    public void update(Emp e) {
        if (e == null || e.getId() == null) throw new BusinessException("员工ID不能为空");
        if (e.getName() != null) validateName(e.getName());
        if (e.getPhone() != null) validatePhone(e.getPhone(), true);
        if (e.getEmail() != null) {
            validateEmail(e.getEmail());
            if (e.getEmail().isBlank()) e.setEmail("");
        }
        if (e.getGender() != null) validateGender(e.getGender());
        if (e.getDeptId() != null || e.getJobId() != null) {
            Emp current = empMapper.selectById(e.getId());
            if (current == null) throw new BusinessException("员工不存在");
            validateDepartmentAndJob(e.getDeptId() == null ? current.getDeptId() : e.getDeptId(),
                    e.getJobId() == null ? current.getJobId() : e.getJobId());
        }
        boolean hasEditableField = (e.getName() != null && !e.getName().isEmpty())
                || e.getGender() != null || (e.getPhone() != null && !e.getPhone().isEmpty())
                || (e.getEmail() != null && !e.getEmail().isEmpty())
                || e.getDeptId() != null || e.getJobId() != null || e.getHireDate() != null;
        if (!hasEditableField) {
            throw new BusinessException("请至少提供一个要修改的员工字段");
        }
        int changed = empMapper.update(e);
        if (changed == 0) throw new BusinessException("员工不存在或没有可修改的字段");
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) throw new BusinessException("员工姓名不能为空");
        if (name.length() > 50) throw new BusinessException("员工姓名不能超过50个字符");
    }

    private void validatePhone(String phone, boolean required) {
        if (phone == null || phone.isBlank()) {
            if (required) throw new BusinessException("手机号不能为空");
            return;
        }
        if (phone.length() > 20 || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException("手机号格式不正确");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) return;
        if (email.length() > 100 || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException("邮箱格式不正确");
        }
    }

    private void validateGender(Integer gender) {
        if (gender != null && gender != 0 && gender != 1) throw new BusinessException("性别值无效");
    }

    private void validateDepartmentAndJob(Long deptId, Long jobId) {
        if (deptId == null || deptMapper.selectById(deptId) == null) throw new BusinessException("请选择有效部门");
        if (jobId == null || jobMapper.selectById(jobId) == null) throw new BusinessException("请选择有效职位");
    }

    @Override
    public void delete(Long id) {
        if (empMapper.deleteById(id) == 0) throw new BusinessException("员工不存在");
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            empMapper.deleteBatch(ids);
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) throw new BusinessException("员工状态值无效");
        if (empMapper.updateStatus(id, status) == 0 && empMapper.selectById(id) == null) {
            throw new BusinessException("员工不存在");
        }
    }
}

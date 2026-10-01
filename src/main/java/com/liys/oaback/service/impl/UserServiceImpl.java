package com.liys.oaback.service.impl;

import com.liys.oaback.common.BusinessException;

import com.liys.oaback.dao.EmpMapper;
import com.liys.oaback.domain.Emp;
import com.liys.oaback.domain.LoginUser;
import com.liys.oaback.service.UserService;
import com.liys.oaback.utils.Md5Util;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;


/**
 * Service implementation for user-related operations.
 * This class implements the UserService interface and provides
 * concrete implementations for user service methods.
 */
@Service
public class UserServiceImpl implements UserService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8083}")
    private String baseUrl;

    @Autowired
    private EmpMapper empMappers;

    // Redis 数据库
    @Override
    public LoginUser login(String userName, String password, HttpSession session) {

        // 调用dao 做登录的查询
        Emp emp = empMappers.selectByUsername(userName);

        // 判断用户名和密码是否正确（注意用 ||，且先判空避免 NPE）
        if (userName == null || password == null || emp == null || emp.getPassword() == null || !emp.getPassword().equals(Md5Util.md5(password))) {
            throw new BusinessException("用户名或密码错误");
        }
        if (emp.getStatus() == null || emp.getStatus() != 1) {
            throw new BusinessException("该用户已被禁用");
        }
        // 把emp对象转换成LoginUser对象
        LoginUser u = toLoginUser(emp);
        // 把LoginUser对象存入session中（key 必须与 LoginInterceptor / info() 保持一致：loginUser）
        u.setToken(UUID.randomUUID().toString());
        session.setAttribute("loginUser", u);

        return u;
    }

    // 获取当前登录用户的信息
    @Override
    public LoginUser info(HttpSession session) {
        LoginUser u = (LoginUser) session.getAttribute("loginUser");
        // 如果用户不存在，抛出未登录异常
        if (u == null) {
            throw new BusinessException("未登录");
        }
        Emp emp = empMappers.selectById(u.getId());
        if (emp == null || emp.getStatus() == null || emp.getStatus() != 1) {
            session.invalidate();
            throw new BusinessException("账号已失效，请重新登录");
        }
        LoginUser latest = toLoginUser(emp);
        latest.setToken(u.getToken());
        session.setAttribute("loginUser", latest);
        return latest;
    }

    @Override
    public void changePassword(String oldPwd, String newPwd, HttpSession session) {

        if (oldPwd == null || oldPwd.isBlank()) throw new BusinessException("请输入原密码");
        if (newPwd == null || newPwd.isBlank()) throw new BusinessException("新密码不能为空");
        if (newPwd.length() < 6 || newPwd.length() > 64) throw new BusinessException("新密码长度必须为6到64位");
        if (oldPwd.equals(newPwd)) throw new BusinessException("新密码不能与原密码相同");

        LoginUser u = info(session);
        // 谁要改这个密码  emp
        Emp emp = empMappers.selectById(u.getId());
        if (emp == null){
            throw new BusinessException("用户不存在");
        }
        if (emp.getPassword() == null || !emp.getPassword().equals(Md5Util.md5(oldPwd))){
            throw new BusinessException("旧密码错误");
        }
        empMappers.updatePassword(u.getId(), Md5Util.md5(newPwd));

    }

    @Override
    public String updateAvatar(MultipartFile file, HttpSession session) {
        LoginUser user = info(session);
        if (file == null || file.isEmpty()) throw new BusinessException("请选择头像文件");
        String contentType = file.getContentType();
        Set<String> allowed = Set.of("image/jpeg", "image/png");
        if (!allowed.contains(contentType)) throw new BusinessException("头像仅支持 JPG 或 PNG 格式");
        if (file.getSize() > 2 * 1024 * 1024) throw new BusinessException("头像不能超过 2MB");
        String originalFilename = file.getOriginalFilename();
        int dot = originalFilename == null ? -1 : originalFilename.lastIndexOf('.');
        String extension = dot < 0 ? ".png" : originalFilename.substring(dot).toLowerCase();
        if (!Set.of(".jpg", ".jpeg", ".png").contains(extension)) throw new BusinessException("头像仅支持 JPG 或 PNG 格式");
        if ("image/png".equals(contentType) != ".png".equals(extension)) throw new BusinessException("头像文件类型与扩展名不匹配");
        try {
            byte[] bytes = file.getBytes();
            boolean pngSignature = bytes.length >= 8 && bytes[0] == (byte) 0x89 && bytes[1] == 0x50
                    && bytes[2] == 0x4e && bytes[3] == 0x47 && bytes[4] == 0x0d
                    && bytes[5] == 0x0a && bytes[6] == 0x1a && bytes[7] == 0x0a;
            boolean jpegSignature = bytes.length >= 3 && bytes[0] == (byte) 0xff
                    && bytes[1] == (byte) 0xd8 && bytes[2] == (byte) 0xff;
            if (!(pngSignature && "image/png".equals(contentType))
                    && !(jpegSignature && "image/jpeg".equals(contentType))) {
                throw new BusinessException("文件内容不是有效的 JPG 或 PNG 图片");
            }
        } catch (IOException e) {
            throw new BusinessException("头像文件读取失败");
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + (".jpeg".equals(extension) ? ".jpg" : extension);
        Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path destination = directory.resolve(filename).normalize();
        if (!destination.startsWith(directory)) throw new BusinessException("头像文件路径无效");
        try {
            Files.createDirectories(directory);
            file.transferTo(destination);
        } catch (IOException e) {
            throw new BusinessException("头像上传失败");
        }
        String url = baseUrl.replaceAll("/+$", "") + "/uploads/" + filename;
        try {
            if (empMappers.updateAvatar(user.getId(), url) == 0) throw new BusinessException("头像更新失败，员工不存在");
        } catch (RuntimeException e) {
            try { Files.deleteIfExists(destination); } catch (IOException ignored) { }
            if (e instanceof BusinessException businessException) throw businessException;
            throw new BusinessException("头像更新失败");
        }
        user.setAvatar(url);
        session.setAttribute("loginUser", user);
        return url;
    }

    // 将Emp对象转换成LoginUser对象
    private LoginUser toLoginUser(Emp emp) {
        LoginUser u = new LoginUser();
        u.setId(emp.getId());
        u.setUsername(emp.getUsername());
        u.setName(emp.getName());
        u.setGender(emp.getGender());
        u.setPhone(emp.getPhone());
        u.setEmail(emp.getEmail());
        u.setAvatar(emp.getAvatar() == null ? "" : emp.getAvatar());
        u.setDeptId(emp.getDeptId());
        u.setJobId(emp.getJobId());
        u.setDeptName(emp.getDeptName() == null ? "" : emp.getDeptName());
        u.setJobName(emp.getJobName() == null ? "" : emp.getJobName());
        u.setHireDate(emp.getHireDate());
        u.setRole(emp.getRole());
        return u;
    }




}

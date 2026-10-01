package com.liys.oaback.service;

import com.liys.oaback.domain.LoginUser;
import jakarta.servlet.http.HttpSession;


public interface UserService {


        /**
         * 获取用户信息
         * @param userId
         * @return
         * HttpSession session   一次会话
         */
        LoginUser login(String userName, String password, HttpSession session);

        LoginUser info(HttpSession session);


        void changePassword(String oldPwd, String newPwd, HttpSession session);

        String updateAvatar(org.springframework.web.multipart.MultipartFile file, HttpSession session);
}

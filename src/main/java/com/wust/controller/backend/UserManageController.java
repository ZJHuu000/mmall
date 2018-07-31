/**
 * FileName: UserManageController
 * Author:   ZJH·Andy
 * Date:     2018/7/31 22:52
 */
package com.wust.controller.backend;

import com.wust.common.Const;
import com.wust.common.ServerResponse;
import com.wust.pojo.User;
import com.wust.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="/login",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse response = iUserService.login(username,password);
        if(response.isSuccess()){
            User user=(User)response.getData();
            if(user.getRole().equals(Const.Role.ROLE_ADMIN)){
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }else{
                return ServerResponse.createByErrorMessage("非管理员，无法登陆！");
            }
        }
        return response;
    }
}

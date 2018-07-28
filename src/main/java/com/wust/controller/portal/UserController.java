/**
 * FileName: UserController
 * Author:   ZJH·Andy
 * Date:     2018/7/28 16:13
 */
package com.wust.controller.portal;

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
@RequestMapping(value="/user")
public class UserController {

    @Autowired
    private IUserService iUserService;
    /**
     * 用户登录验证
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value="/login",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //service层
        ServerResponse<User> response=iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
}

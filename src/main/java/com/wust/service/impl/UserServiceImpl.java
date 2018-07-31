/**
 * FileName: UserServiceImpl
 * Author:   ZJH·Andy
 * Date:     2018/7/28 23:12
 */
package com.wust.service.impl;

import com.wust.common.Const;
import com.wust.common.ServerResponse;
import com.wust.common.TokenCache;
import com.wust.dao.UserMapper;
import com.wust.pojo.User;
import com.wust.service.IUserService;
import com.wust.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount=userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在！");
        }
        //密码登录 md5加密
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误！");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功！",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        //校验用户名
        ServerResponse validResponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //校验邮箱
        validResponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //密码md5加密，存储
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insert(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败！");
        }
        return ServerResponse.createBySuccessMessage("注册成功！");
    }

    @Override
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount=userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在！");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount=userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("邮箱已注册！");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return ServerResponse.createBySuccessMessage("校验成功！");
    }

    @Override
    public ServerResponse selectQuestion(String username) {
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        String question=userMapper.selectQuestionByUsername(username);
        if(StringUtils.isBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("返回的密码是错误的！");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            String forgetToken=UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误！");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要重新传递！");
        }
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或过期！");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5Password=MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount=userMapper.updatePasswordByUsername(username,md5Password);
            if(resultCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功！");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败！");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        int resultCount=userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误！");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount=userMapper.updateByPrimaryKeySelective(user);
        if(resultCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功！");
        }
        return ServerResponse.createByErrorMessage("密码更新失败！");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        int resultCount=userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("该邮箱已注册！");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        resultCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultCount>0){
            return ServerResponse.createBySuccess("更新成功！",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新失败！");
    }

    @Override
    public ServerResponse<User> getInformation(int userId) {
        User user=userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户！");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


}

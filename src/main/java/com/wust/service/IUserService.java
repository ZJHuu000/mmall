/**
 * FileName: IUserService
 * Author:   ZJH·Andy
 * Date:     2018/7/28 16:19
 */
package com.wust.service;

import com.wust.common.ServerResponse;
import com.wust.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username,String password);
}

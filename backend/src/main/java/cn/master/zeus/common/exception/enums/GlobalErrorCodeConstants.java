package cn.master.zeus.common.exception.enums;

import cn.master.zeus.common.exception.ErrorCode;

/**
 * @author Created by 11's papa on 12/12/2023
 **/
public interface GlobalErrorCodeConstants {
    ErrorCode SUCCESS = new ErrorCode(200, "成功");
    // ========== 客户端错误段 ==========

    ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数不正确");
    ErrorCode UNAUTHORIZED = new ErrorCode(401, "账号未登录");
    ErrorCode FORBIDDEN = new ErrorCode(403, "没有该操作权限");
    ErrorCode NOT_FOUND = new ErrorCode(404, "请求未找到");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode(405, "请求方法不正确");
    ErrorCode LOCKED = new ErrorCode(423, "请求失败，请稍后重试"); // 并发请求，不允许
    ErrorCode TOO_MANY_REQUESTS = new ErrorCode(429, "请求过于频繁，请稍后重试");

    // ========== 服务端错误段 ==========

    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常");
    ErrorCode WORKSPACE_NAME_DUPLICATE = new ErrorCode(1002004000, "工作空间名已存在");
    ErrorCode PROJECT_NAME_DUPLICATE = new ErrorCode(200000, "项目名已存在");
    ErrorCode CHECK_OWNER_PROJECT = new ErrorCode(200001, "当前用户没有操作此项目的权限");

    // ========== 用户模块 100200 ==========
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(100200, "用户账号已经存在");
    ErrorCode USER_MOBILE_EXISTS = new ErrorCode(100201, "手机号已经存在");
    ErrorCode USER_EMAIL_EXISTS = new ErrorCode(100202, "邮箱已经存在");
    ErrorCode USER_NOT_EXISTS = new ErrorCode(100203, "用户不存在");
    // ========== 用户模块 100300 ==========
    ErrorCode GROUP_NOT_EXISTS = new ErrorCode(100300, "group does not exist!");
    ErrorCode GROUP_NOT_DELETE = new ErrorCode(100301, "系统用户组不支持删除!");
    ErrorCode GROUP_NAME_DUPLICATE = new ErrorCode(100302, "用户组名称已存在");
}

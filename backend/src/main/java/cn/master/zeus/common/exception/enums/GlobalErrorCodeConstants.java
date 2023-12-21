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
}

package com.litegateway.admin.common.exception;

import com.litegateway.admin.common.ErrorCodeEnum;

/**
 * 错误码接口
 * 从旧项目迁移，包名从 com.jtyjy.gateway 改为 com.litegateway
 */
public interface ErrorCode {
    String getCode();
    String getMessage();

    // 常用错误码常量，便于在 Controller 中直接使用
    ErrorCode SYSTEM_ERROR = ErrorCodeEnum.SYSTEM_ERROR_B0001;
    ErrorCode RESOURCE_NOT_FOUND = ErrorCodeEnum.USER_ERROR_A0404;
}

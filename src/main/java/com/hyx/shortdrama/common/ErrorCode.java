package com.hyx.shortdrama.common;

/**
 * 自定义错误码
 */
public enum ErrorCode {

    SUCCESS(0, "Thành công"),
    PARAMS_ERROR(40000, "Tham số không hợp lệ"),
    NOT_LOGIN_ERROR(40100, "Chưa đăng nhập"),
    NO_AUTH_ERROR(40101, "Không có quyền truy cập"),
    NOT_FOUND_ERROR(40400, "Không tìm thấy dữ liệu"),
    FORBIDDEN_ERROR(40300, "Bị cấm truy cập"),
    SYSTEM_ERROR(50000, "Lỗi hệ thống"),
    OPERATION_ERROR(50001, "Thao tác thất bại");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}

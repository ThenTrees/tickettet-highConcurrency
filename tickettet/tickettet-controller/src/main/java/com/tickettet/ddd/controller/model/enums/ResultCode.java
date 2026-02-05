package com.tickettet.ddd.controller.model.enums;

/**
 * Trả về mã trạng thái
 * Chứ số đầu tiên: 1: Sản phẩm; 2: Người dùng; 3: Giao dịch;
 * 4: Khuyến mãi; 5: Cửa hàng; 6: Trang web; 7: Cài đặt; 8: Khác
 */
public enum ResultCode {
    /**
     * Mã trạng thái thành công
     */
    SUCCESS(200, "Thành công"),

    /**
     * Tham số bất thường
     */
    PARAMS_ERROR(4002, "Tham số bất thường"),

    /**
     * Mã lỗi trả về
     */
    DEMO_SITE_FORBIDDEN_ERROR(4001, "Trang web demo không được phép sử dụng"),

    /**
     * Mã lỗi trả về
     */
    ERROR(4003, "Máy chủ bận, vui lòng thử lại sau"),

    BAD_REQUEST(400, "Yêu cầu không hợp lệ");

    /**
     * Người dùng
     */

    /**
     * Cửa hàng
     */

    /**
     * Sản phẩm
     */

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}

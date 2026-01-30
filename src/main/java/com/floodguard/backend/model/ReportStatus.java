package com.floodguard.backend.model;

public enum ReportStatus {
    UNVERIFIED, // Mới gửi, chưa xác minh (Người dân gửi)
    VERIFIED, // Đã xác minh (Tổ trưởng xác nhận đúng sự thật)
    PROCESSING, // Phường đang xử lý / điều phối
    RESOLVED, // Đã khắc phục xong
    REJECTED // Tin giả / Sai sự thật
}

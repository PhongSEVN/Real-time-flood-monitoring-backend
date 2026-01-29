-- SETUP EXTENSION & ENUMS
CREATE EXTENSION IF NOT EXISTS postgis;

-- Định nghĩa các vai trò trong hệ thống
CREATE TYPE user_role AS ENUM (
    'ADMIN',            -- Quản trị viên hệ thống
    'WARD_OFFICIAL',    -- Cán bộ Phường (người ra quyết định, xem báo cáo tổng hợp)
    'GROUP_LEADER',     -- Trưởng khu phố / Tổ trưởng (người xác thực thông tin tại hiện trường)
    'RESIDENT'          -- Người dân (đăng tin, xem tin)
);

-- Định nghĩa trạng thái xử lý phản ánh
CREATE TYPE report_status AS ENUM (
    'UNVERIFIED',       -- Mới gửi, chưa xác minh (Người dân gửi)
    'VERIFIED',         -- Đã xác minh (Tổ trưởng xác nhận đúng sự thật)
    'PROCESSING',       -- Phường đang xử lý / điều phối
    'RESOLVED',         -- Đã khắc phục xong
    'REJECTED'          -- Tin giả / Sai sự thật
);

-- Định nghĩa loại hạ tầng
CREATE TYPE infra_type AS ENUM (
    'MANHOLE',          -- Hố ga
    'DRAIN_PIPE',       -- Tuyến thoát nước
    'CANAL',            -- Kênh rạch
    'DYKE',             -- Đê bao
    'TRASH_POINT',      -- Điểm tập kết rác
    'TREE',             -- Cây xanh
    'PRODUCTION_AREA'   -- Khu sản xuất/Nông nghiệp
);

-- QUẢN LÝ NGƯỜI DÙNG (USERS)
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE, 
    password_hash VARCHAR(255),
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15), -- Quan trọng để liên lạc khẩn cấp
    role user_role DEFAULT 'RESIDENT',
    
    -- Khu vực quản lý (Dành cho Tổ trưởng/Trưởng khu phố để lọc tin xác thực)
    management_area_code VARCHAR(50), 
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- NHÓM DỮ LIỆU DÂN CƯ & Y TẾ (Cứu trợ & Ưu tiên)
/* 
 * Bảng: HỒ SƠ NHÀ DÂN (Households)
 * Mục đích: Hỗ trợ ra quyết định cứu trợ dựa trên mức độ ưu tiên (người già, bệnh...).
 * Nguồn tham khảo:,,
 */
CREATE TABLE households (
    household_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id), -- Liên kết tài khoản chủ hộ (nếu có)
    address VARCHAR(255) NOT NULL,
    
    -- Thông tin nhân khẩu & Y tế (Để ưu tiên cứu trợ)
    num_people INT DEFAULT 1,
    has_elderly BOOLEAN DEFAULT FALSE,      -- Có người già (>70, 80 tuổi)
    has_children BOOLEAN DEFAULT FALSE,     -- Có trẻ em
    has_sick_person BOOLEAN DEFAULT FALSE,  -- Có người bệnh nền/sốt xuất huyết
    has_pregnant BOOLEAN DEFAULT FALSE,     -- Có phụ nữ mang thai
    
    -- Thông tin tài sản & Kết cấu
    num_floors INT,                         -- Số tầng (để biết có chỗ di dời đồ lên cao không)
    business_type VARCHAR(100),             -- Loại hình kinh doanh (VD: Quán ăn -> xả mỡ gây nghẹt cống)
    asset_description TEXT,                 -- Mô tả tài sản giá trị cần bảo vệ
    
    -- Vị trí không gian (Point)
    geom GEOMETRY(POINT, 4326) NOT NULL
);
CREATE INDEX idx_households_geom ON households USING GIST (geom);

/* 
 * Bảng: LỰC LƯỢNG Y TẾ (Medical Resources)
 * Mục đích: Hiển thị các điểm hỗ trợ y tế khi có sự cố.
 * Nguồn tham khảo:,
 */
CREATE TABLE medical_resources (
    resource_id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,             -- Tên trạm y tế, phòng khám, nhà thuốc
    type VARCHAR(50) CHECK (type IN ('STATION', 'CLINIC', 'PHARMACY', 'DOCTOR')),
    contact_phone VARCHAR(15),
    specialization VARCHAR(100),            -- Chuyên khoa (nếu là Bác sĩ)
    is_active BOOLEAN DEFAULT TRUE,
    geom GEOMETRY(POINT, 4326) NOT NULL
);
CREATE INDEX idx_medical_geom ON medical_resources USING GIST (geom);

-- NHÓM HẠ TẦNG (INFRASTRUCTURE) - BẢN ĐỒ GIÁM SÁT
/*
 * Bảng: HẠ TẦNG (Infrastructure)
 * Mục đích: Quản lý các lớp bản đồ (Layers) như hố ga, cống, đê, cây xanh.
 * Nguồn tham khảo:,
 */
CREATE TABLE infrastructure (
    infra_id SERIAL PRIMARY KEY,
    name VARCHAR(150),                      -- VD: "Hố ga số 5", "Kênh Ba Bò"
    type infra_type NOT NULL,               -- Loại hạ tầng (Enum định nghĩa ở trên)
    
    -- Trạng thái & Bảo trì
    status VARCHAR(50) DEFAULT 'GOOD',      -- GOOD, DAMAGED, BLOCKED
    last_maintenance_date DATE,             -- Ngày nạo vét/gia cố gần nhất
    
    -- Thuộc tính mở rộng (JSONB) để lưu thông tin riêng từng loại
    -- VD Cây xanh: {"leaf_volume": "high"}
    -- VD Đê bao: {"repair_history": ["2020", "2022"]}
    properties JSONB,
    -- Vị trí: Dùng GEOMETRY để lưu được cả Điểm (Hố ga, Cây) và Đường (Cống, Đê)
    geom GEOMETRY(GEOMETRY, 4326) NOT NULL
);
CREATE INDEX idx_infra_geom ON infrastructure USING GIST (geom);
CREATE INDEX idx_infra_type ON infrastructure(type);

-- NHÓM DỮ LIỆU MÔI TRƯỜNG & LỊCH SỬ (AI/DỰ BÁO)
/*
 * Bảng: DỮ LIỆU KHÍ HẬU (Climate Data)
 * Mục đích: Lưu trữ dữ liệu Mưa/Triều (Dự báo & Thực tế) để so sánh lịch sử.
 * Logic: Khi có dự báo mới -> Query tìm dữ liệu cũ tương đồng -> Show ảnh cũ.
 */
CREATE TABLE climate_data (
    data_id SERIAL PRIMARY KEY,
    record_date TIMESTAMP NOT NULL,
    type VARCHAR(20) CHECK (type IN ('RAIN', 'TIDE')), -- Mưa hoặc Triều cường
    value FLOAT NOT NULL,                   -- Lượng mưa (mm) hoặc Mức triều (cm)
    
    is_forecast BOOLEAN DEFAULT FALSE,      -- TRUE: Là tin dự báo, FALSE: Là số liệu thực đo
    description TEXT,                       -- VD: "Đỉnh triều năm 2023", "Báo động 3"
    
    -- Khu vực áp dụng (Nếu phường rộng, có thể chia nhỏ, hoặc để null nếu áp dụng toàn phường)
    geom GEOMETRY(POLYGON, 4326) 
);
CREATE INDEX idx_climate_date ON climate_data(record_date);


-- NHÓM SỰ KIỆN & PHẢN ÁNH (REPORTS) - CORE FEATURE
/*
 * Bảng: PHẢN ÁNH / SỰ KIỆN (Reports)
 * Mục đích: Ghi nhận sự cố từ người dân, hỗ trợ quy trình xác minh.
 * Nguồn tham khảo:,,,
 */
CREATE TABLE reports (
    report_id SERIAL PRIMARY KEY,
    
    -- Thông tin người gửi (Hỗ trợ cả người dùng vãng lai qua Form)
    user_id INT REFERENCES users(user_id),  -- Có thể NULL nếu không đăng nhập
    guest_name VARCHAR(100),                -- Tên người gửi (nếu không login)
    guest_phone VARCHAR(15),                -- SĐT để liên hệ xác minh
    
    -- Phân loại sự kiện
    event_type VARCHAR(50) CHECK (event_type IN ('RAIN', 'TIDE', 'FLOOD', 'DYKE_BREAK', 'OTHER')),
    
    -- Nội dung phản ánh
    description TEXT,                       -- VD: "Nước ngập nửa bánh xe", "Vỡ đê đoạn A"
    damage_level INT DEFAULT 0,             -- Mức độ thiệt hại ước tính (1-5)
    image_url TEXT,                         -- BẮT BUỘC: Ảnh hiện trạng để so sánh quá khứ
    
    -- Quy trình xác minh (Workflow)
    status report_status DEFAULT 'UNVERIFIED',
    verified_by INT REFERENCES users(user_id), -- ID của Tổ trưởng/Cán bộ đã xác minh
    verified_at TIMESTAMP,
    admin_note TEXT,                        -- Ghi chú xử lý của phường
    
    -- Thời gian & Vị trí
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    geom GEOMETRY(POINT, 4326) NOT NULL     -- Vị trí ghim trên bản đồ
);
CREATE INDEX idx_reports_geom ON reports USING GIST (geom);
CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_time ON reports(created_at);

-- VÍ DỤ TRUY VẤN NGHIỆP VỤ (BUSINESS LOGIC EXAMPLES)
/*
 * LOGIC 1: TÍNH NĂNG GỢI NHỚ / AI
 * Kịch bản: Có dự báo triều cường đạt mức 1.7m vào ngày mai.
 * Hệ thống cần tìm: Các hình ảnh ngập lụt trong quá khứ khi triều cường ~ 1.7m.
 */
-- SELECT r.image_url, r.description, r.created_at, c.value as tide_level
-- FROM reports r
-- JOIN climate_data c ON DATE(r.created_at) = DATE(c.record_date)
-- WHERE c.type = 'TIDE'
--   AND c.is_forecast = FALSE -- Lấy dữ liệu thực tế lịch sử
--   AND c.value BETWEEN 1.65 AND 1.75 -- Tìm biên độ gần đúng (+/- 5cm)
--   AND r.event_type = 'FLOOD'
-- ORDER BY r.created_at DESC;

/*
 * LOGIC 2: LỚP BẢN ĐỒ CỨU TRỢ KHẨN CẤP
 * Kịch bản: Tìm các hộ dân cần ưu tiên (người già, bệnh) trong vùng bán kính 200m quanh điểm vỡ đê.
 */
-- SELECT h.address, h.num_people, h.has_elderly, h.has_sick_person, h.phone_owner
-- FROM households h
-- WHERE ST_DWithin(
--     h.geom, 
--     ST_SetSRID(ST_MakePoint(106.75, 10.85), 4326), -- Tọa độ điểm vỡ đê
--     200 -- Bán kính 200m
-- )
-- AND (h.has_elderly = TRUE OR h.has_sick_person = TRUE);

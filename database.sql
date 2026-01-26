
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Người dùng hệ thống (cán bộ, tổ trưởng, admin, người dân đăng nhập)
CREATE TABLE users (
                       id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       full_name       VARCHAR(255),
                       phone           VARCHAR(20),
                       email           VARCHAR(255),
                       role            VARCHAR(50),
    -- citizen | ward_staff | district_staff | admin
                       password_hash   TEXT,
                       created_at      TIMESTAMP DEFAULT NOW()
);

-- Sự kiện thiên tai: mưa lớn, triều cường, vỡ đê...
CREATE TABLE damage_events (
                               id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               event_type      VARCHAR(100),
    -- flood | storm | high_tide | dam_break
                               description     TEXT,
                               start_time      TIMESTAMP,
                               end_time        TIMESTAMP,
                               severity_level  INT CHECK (severity_level BETWEEN 1 AND 5),
                               created_at      TIMESTAMP DEFAULT NOW()
);

-- Vùng bị ảnh hưởng (polygon)
CREATE TABLE damage_areas (
                              id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              event_id        UUID REFERENCES damage_events(id) ON DELETE CASCADE,
                              area_name       VARCHAR(255),
                              geom            GEOMETRY(POLYGON, 4326),
                              risk_level      INT CHECK (risk_level BETWEEN 1 AND 5),
                              estimated_households INT,
                              created_at      TIMESTAMP DEFAULT NOW()
);

-- Báo cáo thiệt hại (có thể ẩn danh)
CREATE TABLE damage_reports (
                                id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- NULL nếu người dân không đăng nhập
                                user_id         UUID REFERENCES users(id) ON DELETE SET NULL,

                                event_id        UUID REFERENCES damage_events(id) ON DELETE CASCADE,
                                area_id         UUID REFERENCES damage_areas(id) ON DELETE SET NULL,

                                reporter_name   VARCHAR(255),
                                reporter_phone  VARCHAR(20),

                                damage_type     VARCHAR(100),
    -- house | crop | livestock | business | infrastructure

                                damage_level    INT CHECK (damage_level BETWEEN 1 AND 5),
                                estimated_loss  BIGINT, -- VNĐ

                                description     TEXT,

                                geom            GEOMETRY(POINT, 4326),

                                is_verified     BOOLEAN DEFAULT FALSE,

                                created_at      TIMESTAMP DEFAULT NOW()
);

-- Chi tiết tài sản bị thiệt hại (vườn mai, nhà, máy móc...)
CREATE TABLE damage_assets (
                               id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               report_id       UUID REFERENCES damage_reports(id) ON DELETE CASCADE,

                               asset_type      VARCHAR(100),
                               asset_name      VARCHAR(255),
                               quantity        INT,
                               unit            VARCHAR(50),
                               estimated_value BIGINT
);

CREATE INDEX idx_damage_areas_geom
    ON damage_areas USING GIST (geom);

CREATE INDEX idx_damage_reports_geom
    ON damage_reports USING GIST (geom);

-- Example event
INSERT INTO damage_events (event_type, description, start_time, severity_level)
VALUES (
           'high_tide',
           'Triều cường gây ngập khu vực Thủ Đức',
           NOW(),
           4
       );

-- Example area
INSERT INTO damage_areas (event_id, area_name, geom, risk_level, estimated_households)
SELECT id,
       'Phường Linh Đông',
       ST_GeomFromText(
               'POLYGON((106.745 10.845, 106.755 10.845, 106.755 10.855, 106.745 10.855, 106.745 10.845))',
               4326
       ),
       4,
       120
FROM damage_events
         LIMIT 1;

-- Example report (anonymous)
INSERT INTO damage_reports (
    event_id,
    area_id,
    reporter_name,
    reporter_phone,
    damage_type,
    damage_level,
    estimated_loss,
    description,
    geom
)
SELECT de.id,
       da.id,
       'Nguyễn Văn A',
       '0909123456',
       'crop',
       5,
       300000000,
       'Vườn mai bị ngập hơn 1m, hư hỏng nặng',
       ST_SetSRID(ST_Point(106.75, 10.85), 4326)
FROM damage_events de
         JOIN damage_areas da ON da.event_id = de.id
    LIMIT 1;

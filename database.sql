CREATE TABLE IF NOT EXISTS "users" (
	"user_id" SERIAL,
	"full_name" VARCHAR(100) NOT NULL,
	"email" VARCHAR(255),
	"phone_number" VARCHAR(15),
	"role" VARCHAR(20) CHECK("[object Object]" IN RESIDENT AND GROUP_LEADER AND WARD_OFFICIAL),
	"priority_level" INTEGER DEFAULT 0,
	"address_group" VARCHAR(100) UNIQUE,
	PRIMARY KEY("user_id")
);




CREATE TABLE IF NOT EXISTS "locations" (
	"location_id" SERIAL,
	"address" VARCHAR(255),
	"geom" BLOB(POINT,4326),
	"location_type" VARCHAR(50),
	"base_elevation" BLOB DEFAULT 0,
	PRIMARY KEY("location_id")
);


CREATE INDEX "idx_locations_geom"
ON "locations" ("geom");

CREATE TABLE IF NOT EXISTS "reports" (
	"report_id" SERIAL,
	"location_id" INTEGER,
	"user_id" INTEGER,
	"report_time" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	"event_type" VARCHAR(50),
	"description" TEXT,
	"image_url" TEXT,
	"damage_level" INTEGER,
	"verify_status" VARCHAR(20) DEFAULT 'PENDING' CHECK("[object Object]" IN PENDING AND VERIFIED AND PROCESSED),
	PRIMARY KEY("report_id")
);




CREATE TABLE IF NOT EXISTS "historical_data" (
	"history_id" SERIAL,
	"year" INTEGER NOT NULL,
	"alert_level" VARCHAR(50),
	"impact_summary" TEXT,
	"reference_geom" BLOB(POLYGON,4326),
	PRIMARY KEY("history_id")
);



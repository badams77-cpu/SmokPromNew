DROP TABLE IF EXISTS access_codes;

CREATE TABLE IF NOT EXISTS access_codes (
id int auto_increment primary key
, access_code text
, user_id int
, request_token text
, access_code_date date
, access_code_used_date date
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

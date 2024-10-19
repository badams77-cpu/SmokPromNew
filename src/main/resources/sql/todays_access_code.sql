DROP TABLE IF EXISTS todays_access_code;

CREATE TABLE IF NOT EXISTS todays_access_code (
id int auto_increment primary key
, user_id int
, day date
, auth_code text
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

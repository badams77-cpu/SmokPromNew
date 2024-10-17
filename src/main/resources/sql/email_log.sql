DROP TABLE IF EXISTS email_log;

CREATE TABLE IF NOT EXISTS email_log (
id int auto_increment primary key
, userid text
, error_message text
, status text
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

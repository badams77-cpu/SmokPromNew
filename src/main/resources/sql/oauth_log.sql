DROP TABLE IF EXISTS oauth_log;

CREATE TABLE IF NOT EXISTS oauth_log (
id int auto_increment primary key
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

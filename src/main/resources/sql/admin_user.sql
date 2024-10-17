DROP TABLE IF EXISTS admin_user;

CREATE TABLE IF NOT EXISTS admin_user (
id int auto_increment primary key
, username text
, firstname text
, lastname text
, passwd text
, secVn int
, last_login datetime
, change_pass_token text
, change_pass_token_created datetime
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

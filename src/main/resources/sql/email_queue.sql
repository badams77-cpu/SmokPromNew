DROP TABLE IF EXISTS email_queue;

CREATE TABLE IF NOT EXISTS email_queue (
id int auto_increment primary key
, receipient_email text
, title_text text
, body_text text
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

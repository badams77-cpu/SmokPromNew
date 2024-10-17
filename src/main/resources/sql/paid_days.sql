DROP TABLE IF EXISTS paid_days;

CREATE TABLE IF NOT EXISTS paid_days (
id int auto_increment primary key
, user_id int
, paid_date date
, has_paid int
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

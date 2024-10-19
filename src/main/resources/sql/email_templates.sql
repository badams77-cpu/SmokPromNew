DROP TABLE IF EXISTS email_templates;

CREATE TABLE IF NOT EXISTS email_templates (
id int auto_increment primary key
, name text
, template text
, subject text
, language text
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

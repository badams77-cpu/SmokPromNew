DROP TABLE IF EXISTS twitter_search;

CREATE TABLE IF NOT EXISTS twitter_search (
id int auto_increment primary key
, userid int
, result_date date
, search_text varchar(80)
, message varchar(250)
, tweet_text varchar(250)
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

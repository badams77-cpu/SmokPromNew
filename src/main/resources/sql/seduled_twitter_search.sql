DROP TABLE IF EXISTS seduled_twitter_search;

CREATE TABLE IF NOT EXISTS seduled_twitter_search (
id int auto_increment primary key
, twitter_search_id int
, user_id int
, trial_search int
, results_date date
, nresult int
, nsent int
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

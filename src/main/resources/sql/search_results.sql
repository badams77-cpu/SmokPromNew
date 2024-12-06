CREATE TABLE IF NOT EXISTS search_results (
                                id int auto_increment primary key,

                                 search_id int,
                                user_id int,
                                tweet_id int,
                                has_paid int,
                                tweeter_user_id int,
                                twitter_user_handle varchar(100),
                                seduled_search_id int,
                                sent int,
    , uuid varchar(80)
    , deleted int
    , deleted_at datetime
    , created_by_userid int
    , updated_by_userid int
    , created datetime
    , updated datetime);

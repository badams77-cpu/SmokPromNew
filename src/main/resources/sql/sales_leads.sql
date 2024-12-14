DROP TABLE IF EXISTS sales_leads;

CREATE TABLE IF NOT EXISTS sales_leads (
                                         id int auto_increment primary key
    , user_id int
    , twitter_handle varchar(100)
    , twitter_user_id long
    , leadStatus enum("UNKNOWN","NEW","LAPSED","FAILED","SUCCES")
    , uuid varchar(80)
    , deleted int
    , deleted_at datetime
    , created_by_userid int
    , updated_by_userid int
    , created datetime
    , updated datetime
    );

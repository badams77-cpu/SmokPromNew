CREATE TABLE IF NOT EXISTS user_prec_tokens (
                                id int auto_increment primary key,

                                id_user int,
                                token varchar(200),
                                token_datetime datetime,
                                locked int


    , uuid varchar(80)
    , deleted int
    , deleted_at datetime
    , created_by_userid int
    , updated_by_userid int
    , created datetime
    , updated datetime);

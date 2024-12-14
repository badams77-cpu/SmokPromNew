DROP TABLE IF EXISTS sales_notes;

CREATE TABLE IF NOT EXISTS sales_notes (
                                         id int auto_increment primary key
    , user_id int
    , sales_lead_id int
    , note_text varchar(100)
    , twitter_user_id long,
    , note_type enum("UNKNOWN","LIKES","DISLIKES","WANTS")
    , uuid varchar(80)
    , deleted int
    , deleted_at datetime
    , created_by_userid int
    , updated_by_userid int
    , created datetime
    , updated datetime
    );

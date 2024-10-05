CREATE DATABASE IF NOT EXISTS smok;

CREATE TABLE IF NOT EXISTS smok.user (
                                         id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                         uuid text,
                                         username text,
                                         firstname text,
                                         lastname text,
                                         secVn int,
                                         subscription_level text,
                                         firstlogin timestamp,
                                         payment_status text,
                                         passwd text,
                                         last_login timestamp default '1970-01-01 00:00:01',
                                         company_name text,
                                         cc_email text,
                                         address1 text,
                                         address2 text,
                                         town text,
                                         country text,
                                         postcode text,
                                         useractive int,
                                         change_pass_token text,
                                         change_pass_token_created text,
                                         change_pass_date timestamp default '1970-01-01 00:00:01',
                                         twitter_handler text,
                                         oauth_reg_token text,
                                         oauth_reg_secret text,
                                         oauth_verifier text,
                                         access_token text,
                                         access_token_expiry text,
                                         updated_by_userid int default 0,
                                         created_by_userid int default 0,
                                         created timestamp default '1970-01-01 00:00:01',
                                         created_by int,
                                         updated timestamp default '1970-01-01 00:00:01',
                                         deleted boolean,
                                         deleted_at timestamp null default null
);

CREATE TABLE IF NOT EXISTS smok.admin_user (
                                               id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                               uuid text,
                                               username text,
                                               firstname text,
                                               lastname text,
                                               secVn int,
                                               passwd text,
                                               useractive int,
                                               updated_by_userid int default 0,
                                               created_by_userid int default 0,
                                               last_login timestamp default '1970-01-01 00:00:01',
                                               change_pass_token text,
                                               change_pass_token_created text,
                                               change_pass_date timestamp default '1970-01-01 00:00:01',
                                               created timestamp default '1970-01-01 00:00:01',
                                               created_by int,
                                               updated timestamp default '1970-01-01 00:00:01',
                                               deleted boolean,
                                               deleted_at timestamp null default null

);

CREATE table IF NOT EXISTS smok.oauth_log (
                                              id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                              userid int,
                                              log_date timestamp,
                                              oauth_status text,
                                              error_message text,
                                              created timestamp default '1970-01-01 00:00:01',
                                              created_by int,
                                              updated timestamp default '1970-01-01 00:00:01',
                                              deleted boolean,
                                              deleted_at timestamp null default null
);

CREATE table IF NOT EXISTS smok.email_queue (
                                           id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                           userid int,
                                           recipient_email text,
                                           title text,
                                           body text,
                                           created timestamp default '1970-01-01 00:00:01',
                                           created_by int,
                                           updated timestamp default '1970-01-01 00:00:01',
                                           deleted boolean,
                                           deleted_at timestamp null default null
);

CREATE table IF NOT EXISTS smok.email_log (
                                         id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                         userid int,
                                         email_id int,
                                         error_message text,
                                         status text,
                                         created timestamp default '1970-01-01 00:00:01',
                                         created_by int,
                                         updated timestamp default '1970-01-01 00:00:01',
                                         deleted boolean,
                                         deleted_at timestamp null default null
);

CREATE table IF NOT EXISTS smok.twitter_search (
                                              id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                              userid int,
                                              regularity text,
                                              result_limit int,
                                              lastrun timestamp,
                                              twitter_query text,
                                              has_geocode boolean,
                                              latitude double,
                                              longitude double,
                                              radius double,
                                              tweet_language text,
                                              created timestamp default '1970-01-01 00:00:01',
                                              created_by int,
                                              updated timestamp default '1970-01-01 00:00:01',
                                              deleted boolean,
                                              deleted_at timestamp null default null
);

CREATE table IF NOT EXISTS smok.search_results (
                                              id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                              userid int,
                                              searchid int,
                                              result_date timestamp,
                                              user_handle text,
                                              title text,
                                              message text,
                                              tweet_language text,
                                              created timestamp default '1970-01-01 00:00:01',
                                              created_by int,
                                              updated timestamp default '1970-01-01 00:00:01',
                                              deleted boolean,
                                              deleted_at timestamp null default null
);

CREATE table IF NOT EXISTS smok.search_log (
                                          id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                          userid int,
                                          searchid int,
                                          result_date timestamp,
                                          nresults int,
                                          nsent int,
                                          nsent_success int,
                                          created timestamp default '1970-01-01 00:00:01',
                                          created_by int,
                                          updated timestamp default '1970-01-01 00:00:01',
                                          deleted boolean,
                                          deleted_at timestamp null default null
);


CREATE table IF NOT EXISTS smok.sent_for_search_results (
                                                       id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                                       userid int,
                                                       searchid int,
                                                       message_text text,
                                                       message_body text,
                                                       created timestamp default '1970-01-01 00:00:01',
                                                       created_by int,
                                                       updated timestamp default '1970-01-01 00:00:01',
                                                       deleted boolean,
                                                       deleted_at timestamp null default null

);

CREATE table IF NOT EXISTS smok.access_codes (
                                                 id int PRIMARY KEY AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT AUTO_INCREMENT,
                                                 user_id int,
                                                access_code varchar(1000),
                                                request_token varchar(1000),
                                                access_code_used_date datetime,
                                                access_code_date datetime,

                                                 created timestamp default '1970-01-01 00:00:01',
                                                 created_by int,
                                                 updated timestamp default '1970-01-01 00:00:01',
                                                 deleted boolean,
                                                 deleted_at timestamp null default null

);
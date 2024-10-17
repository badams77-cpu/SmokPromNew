DROP TABLE IF EXISTS user;

CREATE TABLE IF NOT EXISTS user (
id int auto_increment primary key
, username text
, firstname text
, stripe_payments_active int
, lastname text
, secVn int
, passwd text
, last_login datetime
, company_name text
, cc_email text
, address1 text
, address2 text
, town text
, country text
, postcode text
, change_pass_token text
, change_pass_token_created datetime
, twitter_handler text
, oauth_reg_token text
, oauth_reg_secret text
, oauth_verifier text
, access_token text
, access_token_expiry datetime
, useractive int
, passwd_change_date datetime
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

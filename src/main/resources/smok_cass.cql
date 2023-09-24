CREATE KEY SPACE IF NOT EXISTS smok;

CREATE TABLE IF NOT EXISTS smok.user (
 id UUID PRIMARY KEY,
 username text,
 subscription_level text,
 firstlogin timestamp,
 payment_status text,
 passwd text,
 last_login timestamp,
 company_name text,
 cc_email text,
 address1 text,
 address2 text,
 town text,
 country text,
 postcode text,
 change_pass_token text,
 change_pass_token_created text,
 twitter_handler text,
 oauth_reg_token text,
 oauth_reg_secret text,
 oauth_verifier text,
 access_token text,
 access_token_expiry text,
 created timestamp,
 created_by UUID,
 updated timestamp,
 deleted boolean,
 deleted_at timestamp
);

CREATE TABLE IF NOT EXISTS smok.admin_user (
  id UUID PRIMARY KEY,
  username text,
  passwd text,
  change_pass_token text,
  change_pass_token_created text,
  created timestamp,
  created_by UUID,
  updated timestamp,
  deleted boolean,
  deleted_at timestamp

);

CREATE table IF NOT EXISTS smok.oauth_log (
  id UUID PRIMARY KEY,
  userid UUID,
  log_date timestamp,
  oauth_status text,
  error_message text,
  created timestamp,
  created_by UUID,
  updated timestamp,
  deleted boolean,
  deleted_at timestamp
);

CREATE table IF NOT EXISTS email_queue (
  id UUID PRIAMRY KEY,
  userid UUID,
  recipient_email text,
  title text,
  body text,
  created timestamp,
  created_by UUID,
  updated timestamp,
  deleted boolean,
  deleted_at timestamp
);

CREATE table IF NOT EXISTS email_log (
  id UUID PRIMARY KEY,
  userid UUID,
  email_id UUID,
  error_message text,
  status text,
  created timestamp,
  created_by UUID,
  updated timestamp,
  deleted boolean,
  deleted_at timestamp
);

CREATE table IF NOT EXISTS twitter_search (
  id UUID PRIMARY KEY,
  userid UUID,
  regularity text,
  result_limit int,
  lastrun timestamp,
  twitter_query text,
  has_geocode boolean,
  latitude double,
  longitude double,
  radius double,
  tweet_language text,
  created timestamp,
  created_by UUID,
  updated timestamp,
  deleted boolean,
  deleted_at timestamp
);

CREATE table IF NOT EXISTS search_results (
  id UUID PRIMARY KEY,
  userid UUID,
  searchid UUID,
  result_date timestamp,
  user_handle text,
  title text,
  message text,
  tweet_language text,
  created timestamp,
  created_by UUID,
  updated timestamp,
  deleted boolean,
  deleted_at timestamp
);

CREATE table IF NOT EXISTS search_log (
  id UUID PRIMARY KEY,
  userid UUID,
  searchid UUID,
  result_date timestamp,
  nresults int,
  nsent int,
  nsent_success int,
  created timestamp,
  created_by UUID,
  updated timestamp,
  deleted boolean,
  deleted_at timestamp
);


CREATE table IF NOT EXISTS sent_for_search_results (
  id UUID PRIMARY KEY,
  userid UUID,
  searchid UUID,
  message_text text,
  message_body text,
  created timestamp,
  created_by UUID,
  updated timestamp,
  deleted boolean,
  deleted_at timestamp

);

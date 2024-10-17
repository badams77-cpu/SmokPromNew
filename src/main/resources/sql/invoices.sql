DROP TABLE IF EXISTS invoices;

CREATE TABLE IF NOT EXISTS invoices (
id int auto_increment primary key
, user_id int
, invoice_date date
, payed_date date
, nsent int
, amtCharged double
, amtPaid double
, uuid varchar(80)
, deleted int
, deleted_at datetime
, created_by_userid int
, updated_by_userid int
, created datetime
, updated datetime
);

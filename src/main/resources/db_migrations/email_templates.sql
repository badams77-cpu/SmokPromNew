use promo;

drop table email_templates;

create table if not exists email_templates (
                                               id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                               uuid varchar(80),
                                               name VARCHAR(255) not null,
                                               subject VARCHAR(255) not null,
                                               language VARCHAR(5) not null,
                                               template TEXT(65535) not null,
                                               created timestamp default '1970-01-01 00:00:01',
                                               created_by int,
                                               created_by_userid int,
                                               updated_by_userid int,
                                               updated timestamp default '1970-01-01 00:00:01',
                                               deleted boolean,
                                               deleted_at timestamp null default null
);


insert into email_templates (name, subject, language, template) values ('access_code_email','Vapid Promotions - todays twitter searrch','en','    Dear {username},

        Your Twitter Search have run, now you need to authorise Twitter to send your direct messages and Tweet replies, you can do that by clicking here

        <a href="{access_url">Click Here to Authorise Tweeter for us to send your tweets and DMs</a>'

        Emails was only be sent if you have a paid subscription, so check your subscription before signing into tweeter here.

        Attached is the scheduled report you requested. It is in a zip file locked by the password you gave.
');

ALTER TABLE email_templates
    ADD CONSTRAINT email_templates_U_name_language UNIQUE (name, language);
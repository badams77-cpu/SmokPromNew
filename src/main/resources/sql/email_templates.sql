create table if not exists email_templates (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) not null,
  subject VARCHAR(255) not null,
  language VARCHAR(5) not null,
  template TEXT(65535) not null,
      created datetime not null,
      created_by_userid int,
      updated datetime not null,
      updated_by_userid int,
      deleted tinyint default 0 not null,
      deleted_at datetime default null
--    constraint uk_mpcpay_costs_cost_category UNIQUE (practiceGroupId, category_name, deleted, deleted_at),
--    index (practiceGroupId, category_name)

);


insert into email_templates (name, subject, language, template) values ('scheduled_report_success','Your Completed Scheduled Report - MPC','en','    Dear Sir/Madam,

        Your report "{reportName}" started at {startTime} and finished successfully

        Attached is the scheduled report you requested. It is in a zip file locked by the password you gave.
') , ('scheduled_report_failed','Your Failed Scheduled Report - MPC','en', '    We are sorry your report "{reportName}" started at {startTime} failed after {runtime} minutes Please try again with less practices or short date range.
    Full cause details are stored at URCompliant to investigate further.');



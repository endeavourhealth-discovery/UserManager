USE user_manager;

DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role_type;
DROP TABLE IF EXISTS job_category;
DROP TABLE IF EXISTS user_role_project;
DROP TABLE IF EXISTS user_access_profile;
DROP TABLE IF EXISTS role_type_access_profile;
DROP TABLE IF EXISTS application_access_profile;
DROP TABLE IF EXISTS application;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS audit;
DROP TABLE IF EXISTS active_item;


CREATE TABLE user_role
(
	id varchar(36) NOT NULL,
	user_id varchar(36) NOT NULL,
	role_type_id varchar(36) NOT NULL,
	organisation_id varchar(36) NOT NULL,
	user_access_profile_id varchar(36) NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A role which is assigned to a user, linking them to an organisation, project and access profile';

CREATE INDEX ix_user_id
	ON user_role (user_id);


CREATE TABLE role_type
(
	id varchar(36) NOT NULL,
	name varchar(100) NOT NULL,
	description varchar(1000) NOT NULL,
	job_category_id varchar(36) NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A type of role that can be assigned to and performed by an individual user';


CREATE TABLE job_category
(
	id varchar(36) NOT NULL,
	code varchar(5) NOT NULL,
	name varchar(100) NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A job category list (referenced from the NHS national list) which are linked into role types';


CREATE TABLE user_role_project
(
	user_role_id varchar(36) NOT NULL,
	project_id varchar(36) NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_user_role_id_project_id PRIMARY KEY (user_role_id, project_id)
)comment 'Projects enabled for a user role.  A user role may have one or many projects available via their linked organisation';


CREATE TABLE user_access_profile
(
	id varchar(36) NOT NULL,
	application_access_profile_id varchar(36) NOT NULL,
	profile_tree text NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A user access profile derived from application.application_access_profile.
This may be the original profile structure chosen at assignment, or a modification specific to the user’s needs';


CREATE TABLE role_type_access_profile
(
	role_type_id varchar(36) NOT NULL,
	application_access_profile_id varchar(36) NOT NULL,
	profile_tree text NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (role_type_id)
)comment 'A Role Type’s default access profile which is presented as the default when a role is selected for assignment';



CREATE TABLE application_access_profile
(
	id varchar(36) NOT NULL,
	name varchar(100) NOT NULL,
	application_id varchar(36) NOT NULL,
	profile_tree text NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'Access profiles for a particular application. One application may have many profile definitions';


CREATE TABLE application
(
	id varchar(36) NOT NULL,
	name varchar(100) NOT NULL,
	application_tree text NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A list of applications together with their full application/module/options tree';


CREATE TABLE project
(
	id varchar(36) NOT NULL,
	name varchar(100) NOT NULL,
	organisation_id varchar(36) NOT NULL,
	available_to_parents boolean NOT NULL,
	definition text NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A list of project definitions available to assign to users';

CREATE TABLE audit
(
    id varchar(36),
    organisation_id varchar(36),
    timestamp datetime,
    end_user_id varchar(36),
    CONSTRAINT pk_item_type PRIMARY KEY (id, organisation_id, timestamp)
);

CREATE INDEX ix_audit_organisation_timestamp_id
ON audit (organisation_id, timestamp, id);

CREATE TABLE active_item
(
    item_id varchar(36),
    audit_id varchar(36),
    item_type_id int,
    is_deleted boolean,
    organisation_id varchar(36),
    CONSTRAINT pk_active_item PRIMARY KEY (item_id)
);

CREATE INDEX ix_active_item_organisation_type_deleted
ON active_item (organisation_id, item_type_id, is_deleted);

CREATE INDEX ix_active_item_type_deleted_organisation
ON active_item (item_type_id, is_deleted, organisation_id);

CREATE INDEX ix_active_item_organisation_deleted_type
ON active_item (organisation_id, is_deleted, item_type_id);

CREATE INDEX ix_active_item_item_organisation_type
ON active_item (item_id, organisation_id, item_type_id);

CREATE INDEX ix_active_item_audit_organisation_type
ON active_item (audit_id, organisation_id, item_type_id);
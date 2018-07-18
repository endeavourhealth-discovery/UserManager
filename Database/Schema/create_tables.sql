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
DROP TABLE IF EXISTS delegation_relationship;
DROP TABLE IF EXISTS delegation;


CREATE TABLE user_role
(
	id varchar(36) NOT NULL,
	user_id varchar(36) NOT NULL,
	role_type_id varchar(36) NULL,
	organisation_id varchar(36) NOT NULL,
	user_access_profile_id varchar(36) NULL,
    is_deleted boolean null,

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

CREATE TABLE delegation
(
	uuid varchar(36) NOT NULL comment 'the UUID of the delegation',
	name varchar(100) NOT NULL comment 'The name of the delegation',
    root_organisation varchar(36) NULL comment 'the root organisation for the delegation',

	CONSTRAINT user_manager_delegation_pk primary key (uuid)
) comment 'holds the relationships between organisations to display the delegation of creating users and super users';

CREATE INDEX ix_delegation_name
ON delegation (name);

CREATE TABLE delegation_relationship
(
	delegation varchar(36) NOT NULL comment 'the delegation that this relationship belongs to',
	parent_uuid varchar(36) NOT NULL comment 'the UUID of the parent in the delegation',
	parent_type smallint NOT NULL comment 'The type of the parent eg. organisation, region',
	child_uuid varchar(36) NOT NULL comment 'the UUID of the child in the delegation',
    child_type smallint NOT NULL comment 'The type of the child eg. organisation, region',
	include_all_children boolean NOT NULL comment 'Whether to include all children and future children',
	create_super_users boolean NOT NULL comment 'Whether the parent can create super users for the children',
	create_users boolean NOT NULL comment 'Whether the parent can create users for the children',

	CONSTRAINT user_manager_delegation_relationship_pk primary key  (delegation, child_uuid, child_type, parent_uuid, parent_type)
) comment 'holds the relationships between organisations to display the delegation of creating users and super users';


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
USE user_manager;

DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role_type;
DROP TABLE IF EXISTS job_category;
DROP TABLE IF EXISTS user_role_project;
DROP TABLE IF EXISTS user_access_profile;
DROP TABLE IF EXISTS role_type_access_profile;
DROP TABLE IF EXISTS application_policy;
DROP TABLE IF EXISTS application_policy_attribute;
DROP TABLE IF EXISTS application_access_profile;
DROP TABLE IF EXISTS application;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS audit;
DROP TABLE IF EXISTS active_item;
DROP TABLE IF EXISTS delegation_relationship;
DROP TABLE IF EXISTS delegation;
DROP TABLE IF EXISTS user_project;

CREATE TABLE user_project
(
	id varchar(36) NOT NULL,
	user_id varchar(36) NOT NULL,
	organisation_id varchar(36) NOT NULL,
	project_id varchar(36) NULL,
    is_default boolean null default 0,
    is_deleted boolean null,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A project which is assigned to a user which is linked to an organisation';


CREATE TABLE user_role
(
	id varchar(36) NOT NULL,
	user_id varchar(36) NOT NULL,
	role_type_id varchar(36) NULL,
	organisation_id varchar(36) NOT NULL,
	user_access_profile_id varchar(36) NULL,
    is_default boolean null default 0,
    is_deleted boolean null,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A role which is assigned to a user, linking them to an organisation, project and access profile';

CREATE INDEX ix_user_id
	ON user_role (user_id);
    
    
-- RENAME TABLE role_type to application_policy;

CREATE TABLE application_policy
(
	id varchar(36) NOT NULL,
	name varchar(100) NOT NULL,
	description varchar(1000) NOT NULL,
	job_category_id varchar(36) NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A policy that contains groupings of application access attributes';


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

-- RENAME TABLE role_type_access_profile to application_policy_attribute;

-- ALTER TABLE application_policy_attribute CHANGE role_type_id application_policy_id varchar(36) NOT NULL;
CREATE TABLE application_policy_attribute
(
	id varchar(36) NOT NULL,
	application_policy_id varchar(36) NOT NULL,
	application_access_profile_id varchar(36) NOT NULL,
	profile_tree text NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'A mapping table between application policy and application attributes';


CREATE TABLE application_access_profile
(
	id varchar(36) NOT NULL,
	name varchar(100) NOT NULL,
    description varchar(500) NOT NULL,
	application_id varchar(36) NOT NULL,
	profile_tree text NOT NULL,
    is_deleted boolean,

	CONSTRAINT pk_id PRIMARY KEY (id)
)comment 'Access profiles for a particular application. One application may have many profile definitions';


CREATE TABLE application
(
	id varchar(36) NOT NULL,
	name varchar(100) NOT NULL,
	description varchar(500) NOT NULL,
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
    is_deleted boolean,

	CONSTRAINT user_manager_delegation_pk primary key (uuid)
) comment 'holds the relationships between organisations to display the delegation of creating users and super users';

CREATE INDEX ix_delegation_name
ON delegation (name);

CREATE TABLE delegation_relationship
(
	uuid varchar(36) NOT NULL comment 'unique identifier for the relationship',
	delegation varchar(36) NOT NULL comment 'the delegation that this relationship belongs to',
	parent_uuid varchar(36) NOT NULL comment 'the UUID of the parent in the delegation',
	parent_type smallint NOT NULL comment 'The type of the parent eg. organisation, region',
	child_uuid varchar(36) NOT NULL comment 'the UUID of the child in the delegation',
    child_type smallint NOT NULL comment 'The type of the child eg. organisation, region',
	include_all_children boolean NOT NULL comment 'Whether to include all children and future children',
	create_super_users boolean NOT NULL comment 'Whether the parent can create super users for the children',
	create_users boolean NOT NULL comment 'Whether the parent can create users for the children',
    is_deleted boolean,
    

	CONSTRAINT user_manager_delegation_relationship_pk primary key  (uuid)
) comment 'holds the relationships between organisations to display the delegation of creating users and super users';

CREATE INDEX ix_delegation_relationship_delegation_child_parent
ON delegation_relationship (delegation, child_uuid, child_type, parent_uuid, parent_type);


-- ALTER TABLE audit CHANGE user_role_id user_project_id varchar(36) NOT NULL;
CREATE TABLE audit
(
    id varchar(36),
    user_project_id varchar(36),
    timestamp datetime,
    audit_type tinyint,
    item_before varchar(36),
    item_after varchar(36),
    item_type tinyint,
    audit_json text,
    CONSTRAINT pk_item_type PRIMARY KEY (id, user_role_id, timestamp)
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


CREATE TABLE audit_action
(
    id tinyint,
    action_type varchar(50),
    CONSTRAINT pk_audit_action PRIMARY KEY (id)
);

CREATE TABLE item_type
(
    id tinyint,
    item_type varchar(50),
    CONSTRAINT pk_item_type PRIMARY KEY (id)
);
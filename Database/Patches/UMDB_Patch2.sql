

alter table user_manager.audit
add column user_id varchar(36) null;

alter table user_manager.audit
add column organisation_id varchar(36) null;

alter table user_manager.audit
add column project_id varchar(36) null;

update user_manager.audit a
join user_manager.user_project up on up.id = a.user_project_id
set a.user_id = up.user_id,
    a.organisation_id = up.organisation_id,
    a.project_id = up.project_id;

alter table user_manager.audit
drop column user_project_id;
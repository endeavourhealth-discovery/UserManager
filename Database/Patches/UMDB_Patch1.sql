

alter table user_manager.application_policy_attribute
drop column profile_tree;

alter table user_manager.application_access_profile
drop column profile_tree;

alter table user_manager.application_access_profile
add column super_user boolean not null default false;

update user_manager.application_access_profile 
set super_user = true
where is_deleted = 0 and lower(name) like '%super user%';

insert into user_manager.item_type
select 11, 'User Password Email';
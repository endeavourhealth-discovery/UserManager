

select * from user_manager.role_type;

insert into user_manager.audit_action (id, action_type)
values 
(0, 'Add'),
(1, 'Edit'),
(2, 'Delete');

delete from user_manager.item_type;
insert into user_manager.item_type (id, item_type)
values 
(0, 'Role'),
(1, 'User'),
(2, 'Delegation'),
(3, 'Delegation relationship'),
(4, 'Default role change');

insert into user_manager.role_type (id, name, description, job_category_id, is_deleted)
values 
('00972413-8f50-11e8-839e-80fa5b320513', 'User', 'Ability to log on to applications only', 'f0be26ef-8f18-11e8-839e-80fa5b320513', 0),
('f0bc6f4a-8f18-11e8-839e-80fa5b320513', 'Super User', 'Abilty to add, edit and delete users', 'f0bc6fc8-8f18-11e8-839e-80fa5b320513', 0),
('3517dd59-9ecb-11e8-9245-80fa5b320513', 'God Mode', 'Full access to everything', '45626549-9ecb-11e8-9245-80fa5b320513', 0)


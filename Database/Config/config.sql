use config;

insert into config (app_id, config_id, config_data)
values ('user-manager','application',
      '{ "appUrl" : "http://localhost:8080" }'
      );

insert into config (app_id, config_id, config_data)
values ('user-manager','database',
      '{
   "url" : "jdbc:mysql://localhost:3306/user_manager",
   "username" : "USERNAME",
   "password" : "PASSWORD"
}'
);
 
insert into config (app_id, config_id, config_data)
values ('user-manager','keycloak',
'{
   "realm": "endeavour2",
   "realm-public-key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvi7bZ3cX5wA412sbm0Rk2aAuOEjZuMdrSnRtSDCsebVzu4MLu+HZlbYLt7Mpswnc1/MJnatJE+zoraVhkNNrikKTImp2AraCFgz5cf2Xw2M6yRNSSeLatN8E4k8cMAThD7pKzbvRUOuX8l3ez0TssMNvhzEksEDcqVhb5hRE3AHhmkXHeBtqrwG0S+RpOmp5UWeOLy3Zi9QNAACkOd0a1aE65frW0Wm2QXVHeII1AqKLi99f60ktMwhC36DYlzb6aqTiquixl8/mnkZB0Yh82/7xTbqKzdI+yeCFGdUrkELBmg03bjogf0BaWa7yv4vG6mKPgr5iDkrxLZYd+8z9ZQIDAQAB",
   "auth-server-url": "https://devauth.endeavourhealth.net/auth",
   "ssl-required": "external",
   "resource": "eds-dsa-manager",
   "public-client": true
 }'
);

insert into config.config
select 'user-manager', 'keycloak_proxy',
'{
  "user" : "eds-ui",
  "pass" : "bd285adbc36842d7a27088e93c36c13e29ed69fa63a6",
  "url" : "https://devauth.endeavourhealth.net/auth"
 }';
 
  
insert into config (app_id, config_id, config_data)
values ('user-manager','machine_user_realm',
'DiscoveryAPI'
);
 
 
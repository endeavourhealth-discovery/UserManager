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
  "realm": "endeavour",
  "realm-public-key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7GdjckqAZgjxp/o7717ei5RgkW3mtG3W+LfmlboBt20NQ/Jz6yb00Xoe9dBCLsqiiompePWuBNxGdwUNHzJcng7hpTvsi7Zp8PtTJDts/EinroKEv+Gac2VB1k8aLneDOtU6FYdi7uQ4vVU4xJ4D4s1ubG0VQXqUnSUvwwRN5UDdGYLrV2KueajgsNJ3mML4aJ2rLDyUF5uvKQV1UbZAwvCUo0tIeUYoN6PMkpaUrBagWeLhNhrNU9HsiDbMUjVttDRgMlgCtYvu4GapI+0cVecAUWfg0MdTCYuUJwUtTZoatf3d2bietsS+cYPFfs9eCIm1/7GLZWwv6qFDN1a4ewIDAQAB",
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
 }'
 
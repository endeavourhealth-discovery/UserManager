
/*data_sharing_manager.flow_direction*/
insert into data_sharing_manager.flow_direction (id, direction)
values (0, "Inbound");

insert into data_sharing_manager.flow_direction (id, direction)
values (1, "Outbound");

/*data_sharing_manager.flow_schedule*/
insert into data_sharing_manager.flow_schedule (id, flow_schedule)
values (0, "Daily");

insert into data_sharing_manager.flow_schedule (id, flow_schedule)
values (1, "On Demand");

/*data_sharing_manager.data_exchange_method*/
insert into data_sharing_manager.data_exchange_method (id, data_exchange_method)
values (0, "Paper");

insert into data_sharing_manager.data_exchange_method (id, data_exchange_method)
values (1, "Electronic");

/*data_sharing_manager.flow_status*/
insert into data_sharing_manager.flow_status (id, flow_status)
values (0, "In Development");

insert into data_sharing_manager.flow_status (id, flow_status)
values (1, "Live");

/*data_sharing_manager.dsa_status*/
insert into data_sharing_manager.dsa_status (id, dsa_status)
values (0, "Active");

insert into data_sharing_manager.dsa_status (id, dsa_status)
values (1, "Inactive");

/*data_sharing_manager.storage_protocol*/
insert into data_sharing_manager.storage_protocol (id, storage_protocol)
values (0, "Audit Only");

insert into data_sharing_manager.storage_protocol (id, storage_protocol)
values (1, "Temporary Store and Forward");

insert into data_sharing_manager.storage_protocol (id, storage_protocol)
values (2, "Permanent Record Store");

/*data_sharing_manager.nature_of_information*/
insert into data_sharing_manager.nature_of_information (id, nature_of_information)
values (0, "Personal");

insert into data_sharing_manager.nature_of_information (id, nature_of_information)
values (1, "Personal Sensitive");

insert into data_sharing_manager.nature_of_information (id, nature_of_information)
values (2, "Commercial");

/*data_sharing_manager.format_type*/
insert into data_sharing_manager.format_type (id, format_type)
values (0, "Removable Media");

insert into data_sharing_manager.format_type (id, format_type)
values (1, "Electronic Structured Data");

/*data_sharing_manager.data_subject_type*/
insert into data_sharing_manager.data_subject_type (id, data_subject_type)
values (0, "Patient");

/*data_sharing_manager.review_cycle*/
insert into data_sharing_manager.review_cycle (id, review_cycle)
values (0, "Annually");

insert into data_sharing_manager.review_cycle (id, review_cycle)
values (1, "Monthly");

insert into data_sharing_manager.review_cycle (id, review_cycle)
values (2, "Weekly");

/*data_sharing_manager.map_type*/
insert into data_sharing_manager.map_type (id, map_type)
values (0, "Service");

insert into data_sharing_manager.map_type (id, map_type)
values (1, "Organisation");

insert into data_sharing_manager.map_type (id, map_type)
values (2, "Region");

insert into data_sharing_manager.map_type (id, map_type)
values (3, "Data Sharing Agreement");

insert into data_sharing_manager.map_type (id, map_type)
values (4, "Data Flow");

insert into data_sharing_manager.map_type (id, map_type)
values (5, "Data Processing Agreement");

insert into data_sharing_manager.map_type (id, map_type)
values (6, "Cohort");

insert into data_sharing_manager.map_type (id, map_type)
values (7, "Data Set");

insert into data_sharing_manager.map_type (id, map_type)
values (8, "Publisher");

insert into data_sharing_manager.map_type (id, map_type)
values (9, "Subscriber");

insert into data_sharing_manager.map_type (id, map_type)
values (10, "Purpose");

insert into data_sharing_manager.map_type (id, map_type)
values (11, "Benefit");

insert into data_sharing_manager.map_type (id, map_type)
values (12, "Document");


/*data_sharing_manager.security_infrastructure*/
insert into data_sharing_manager.security_infrastructure (id, security_infrastructure)
values (0, "N3");

insert into data_sharing_manager.security_infrastructure (id, security_infrastructure)
values (1, "PSN");

insert into data_sharing_manager.security_infrastructure (id, security_infrastructure)
values (2, "Internet");

/*data_sharing_manager.security_architecture*/
insert into data_sharing_manager.security_architecture (id, security_architecture)
values (0, "TLS/MA");

insert into data_sharing_manager.security_architecture (id, security_architecture)
values (1, "Secure FTP");

/*data_sharing_manager.ConsentModel*/
insert into data_sharing_manager.consent_model (id, consent_model)
values (0, "Explicit Consent");

insert into data_sharing_manager.consent_model (id, consent_model)
values (1, "Implied Consent");

/*data_sharing_manager.data_sharing_agreement*/
insert into data_sharing_manager.data_sharing_agreement (uuid, name, description, dsa_status_id, consent_model_id)
values ("e8340789-0a61-11e7-bc48-80fa5b27a530", "National Data Sharing Agreement", "Full country sharing agreement", 1, 0);


/*data_sharing_manager.data_processing_agreement*/
insert into data_sharing_manager.data_processing_agreement (uuid, name, description, dsa_status_id)
values ("0140a2f8-0a63-11e7-bc48-80fa5b27a530", "National Data Processing Agreement", "Full Country Processing Agreement", 0);

/*data_sharing_manager.data_sharing_summary*/
insert into data_sharing_manager.data_sharing_summary (uuid, name, description, nature_of_information_id, format_type_id, data_subject_type_id, review_cycle_id)
values ("138024c9-0aee-11e7-926e-80fa5b27a530", "National Data Summary", "Sharing data for all patients", 0, 1, 0, 1);

/*data_sharing_manager.cohort*/
insert into data_sharing_manager.cohort (uuid, name, nature)
values ("db64e478-0a3d-11e7-bc48-80fa5b27a530", "All Patients", "Sharing Data");

insert into data_sharing_manager.data_flow (uuid, name, direction_id, flow_schedule_id, approximate_volume, data_exchange_method_id, flow_status_id, storage_protocol_id, security_infrastructure_id, security_architecture_id)
values ("2ea68a0b-0a3e-11e7-bc48-80fa5b27a530", "Endeavour Data Flow", 0, 1, 200000, 1, 0, 0, 0, 0);
    
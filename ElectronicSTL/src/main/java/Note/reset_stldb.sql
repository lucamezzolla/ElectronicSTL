truncate stldb_test.stl_customers;
truncate stldb_test.stl_defects;
truncate stldb_test.stl_simulators;
truncate stldb_test.stl_pages;
truncate stldb_test.stl_simulator_status_values;
truncate stldb_test.stl_periodical_test_values;
truncate stldb_test.stl_users;
insert into stldb_test.stl_users (name, lastname, phone, email, password, level_id) 
values ("Luca", "Mezzolla", "+393312593858", "luca.mezzolla@urbe.aero", md5("trustno1"), "5");
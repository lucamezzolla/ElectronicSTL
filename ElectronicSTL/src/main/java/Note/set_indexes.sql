CREATE UNIQUE INDEX datepage_index ON stldb_test.stl_pages (simulator_id, date_page);
CREATE UNIQUE INDEX simulator_status_values_index ON stldb_test.stl_simulator_status_values (simulator_id, datetime_start);
CREATE UNIQUE INDEX user_index ON stldb_test.stl_users (name, lastname);
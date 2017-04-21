CREATE DATABASE IF NOT EXISTS asv_prueba;

use asv_prueba;

CREATE TABLE IF NOT EXISTS user (
    name varchar(250),
    pass varchar(250) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY(name)
);

CREATE TABLE IF NOT EXISTS server(
	id int NOT NULL AUTO_INCREMENT,
	name varchar(250) NOT NULL,
	ip varchar(45) NOT NULL,
	admin_id varchar(250) NOT NULL,
    CONSTRAINT pk_server PRIMARY KEY(id),
	CONSTRAINT fk_server_user FOREIGN KEY (admin_id) REFERENCES user(name)
);

CREATE TABLE IF NOT EXISTS connection(
	id int NOT NULL AUTO_INCREMENT ,
	name varchar(250) NOT NULL,
	server_id int NOT NULL,
    CONSTRAINT pk_connection PRIMARY KEY(id),
    CONSTRAINT fk_conn_server FOREIGN KEY (server_id) REFERENCES server(id)
);

CREATE TABLE IF NOT EXISTS conn_user(
	user_id varchar(250),
	conn_id int,
	CONSTRAINT pk_conn_user PRIMARY KEY(user_id, conn_id),
	CONSTRAINT fk_connuser_user FOREIGN KEY (user_id) REFERENCES user(name),
	CONSTRAINT fk_connuser_conn FOREIGN KEY (conn_id) REFERENCES connection(id)
);
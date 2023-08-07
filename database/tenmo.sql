BEGIN TRANSACTION;
DROP TABLE IF EXISTS tenmo_user, account, transfer, account_transfer;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transfer_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;
  
CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(55) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;
CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance numeric(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);
-- Sequence to start transfer_id values at 3001 instead of 1
CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;
  
CREATE TABLE transfer (
	transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
	from_user varchar(50) NOT NULL,
	from_account int NOT NULL,
	to_user varchar(50) NOT NULL,
	to_account int NOT NULL,
	transfer_amount numeric(13, 2) NOT NULL,
	transfer_date date NOT NULL,
	status varchar(50) NOT NULL,
	transfer_type varchar(50) NOT NULL,
	CONSTRAINT PK_transfer PRIMARY KEY(transfer_id),
	CONSTRAINT FK_transfer_from_account FOREIGN KEY (from_account) REFERENCES account (account_id),
	CONSTRAINT FK_transfer_to_account FOREIGN KEY (to_account) REFERENCES account (account_id),
	CONSTRAINT FK_transfer_from_user FOREIGN KEY (from_user) REFERENCES tenmo_user (username),
	CONSTRAINT FK_transfer_to_user FOREIGN KEY (to_user) REFERENCES tenmo_user (username)
);

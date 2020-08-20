CREATE TABLE user
(
	user_id UUID NOT NULL,
	user_name VARCHAR(255) NOT NULL,
	user_pass VARCHAR(255) NOT NULL,
	faculty_id UUID NOT NULL,
	PRIMARY KEY (user_id ASC),
	CONSTRAINT fk_state_user FOREIGN KEY (faculty_id) REFERENCES faculty (faculty_id)
);

CREATE TABLE faculty (
	faculty_id UUID NOT NULL,
	faculty_name VARCHAR(255) NOT NULL,
	university VARCHAR(255) NOT NULL,
	JSON_blob_storage_link VARCHAR(255) NULL,
	container_name VARCHAR(255) NULL,
	PRIMARY KEY (faculty_id ASC)
);
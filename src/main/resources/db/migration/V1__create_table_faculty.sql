CREATE TABLE IF NOT EXISTS faculty
(
	id UUID UNIQUE NOT NULL,
	name VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password  VARCHAR(255) NOT NULL,
	university VARCHAR(255) NOT NULL,
	JSON_blob_storage_link VARCHAR(255) NULL,
	config_file_name VARCHAR(255) NULL,
	container_name VARCHAR(255) NULL,
	landing_page_link VARCHAR(255) NULL,
	PRIMARY KEY (id)
);
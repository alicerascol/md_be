CREATE TABLE IF NOT EXISTS student
(
	id UUID UNIQUE NOT NULL,
	director VARCHAR(255) NOT NULL,
	email VARCHAR(255) UNIQUE NOT NULL,
	status VARCHAR(255) NOT NULL,
	faculty_id UUID REFERENCES faculty(id),
	first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    father_initials VARCHAR(255) NOT NULL,
    citizenship VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

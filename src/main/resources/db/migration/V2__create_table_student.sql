CREATE TABLE student
(
	student_id UUID NOT NULL,
	documents_link VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	faculty_id UUID REFERENCES faculty(faculty_id),
	PRIMARY KEY (student_id ASC)
);

create table student_faculty
(
  student_id UUID,
  faculty_id UUID,
  CONSTRAINT student_faculty PRIMARY KEY (student_id, faculty_id),
  CONSTRAINT FK_student
      FOREIGN KEY (student_id) REFERENCES student (id),
  CONSTRAINT FK_faculty
      FOREIGN KEY (faculty_id) REFERENCES faculty (id)
);
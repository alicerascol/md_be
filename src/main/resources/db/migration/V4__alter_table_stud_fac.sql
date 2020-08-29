alter table stud_fac_table
  add  CONSTRAINT FK_first_key FOREIGN KEY (stud_id) REFERENCES student (id);

alter table stud_fac_table
  add  CONSTRAINT FK_second_key FOREIGN KEY (fac_id) REFERENCES faculty (id);

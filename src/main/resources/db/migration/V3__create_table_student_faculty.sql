CREATE TABLE IF NOT EXISTS stud_fac_table
(
  studid UUID,
  facid UUID,
  status VARCHAR(255),
  PRIMARY KEY (studid, facid)
);
CREATE TABLE STATISTIC_DATA (
	ID SERIAL PRIMARY KEY,
	APP VARCHAR(100) NOT NULL,
	URI VARCHAR(100) NOT NULL,
	IP VARCHAR(15) NOT NULL,
	TIME TIMESTAMP
);
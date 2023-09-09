CREATE TABLE USERS (
	ID SERIAL PRIMARY KEY,
	NAME VARCHAR(250) NOT NULL,
	EMAIL VARCHAR(254) NOT NULL,
	CONSTRAINT UNIQ_EMAIL UNIQUE (EMAIL)
);

CREATE TABLE CATEGORIES (
	ID SERIAL PRIMARY KEY,
	NAME VARCHAR(50) NOT NULL,
	CONSTRAINT UNIQ_NAME UNIQUE (NAME)
);

CREATE TABLE EVENTS (
	ID SERIAL PRIMARY KEY,
	ANNOTATION VARCHAR(2000) NOT NULL,
	CATEGORY INTEGER,
	CONFIRMED_REQUEST INTEGER,
	CREATED TIMESTAMP,
	DESCRIPTION VARCHAR(7000) NOT NULL,
	EVENT_DATE TIMESTAMP,
    LAT FLOAT,
    LON FLOAT,
	PAID BOOLEAN NOT NULL,
	PARTICIPANT_LIMIT INTEGER NOT NULL,
	PUBLISHED TIMESTAMP,
	REQUEST_MODERATION BOOLEAN NOT NULL,
	STATE VARCHAR(20),
	TITLE VARCHAR(120),
	VIEWS INTEGER,
    INITIATOR INTEGER,
    FOREIGN KEY (INITIATOR) REFERENCES USERS (ID),
    FOREIGN KEY (CATEGORY) REFERENCES CATEGORIES(ID)
);

CREATE TABLE REQUESTS (
	ID SERIAL PRIMARY KEY,
	CREATED TIMESTAMP,
	EVENT INTEGER,
	REQUESTER INTEGER,
	STATUS VARCHAR(255),
    FOREIGN KEY (REQUESTER) REFERENCES USERS (ID),
    FOREIGN KEY (EVENT) REFERENCES EVENTS(ID)
);

CREATE TABLE COMPILATIONS (
	ID SERIAL PRIMARY KEY,
	PINNED BOOLEAN,
	TITLE VARCHAR(50)
);

CREATE TABLE COMPILATION_EVENTS (
    compilation_id INTEGER,
    event_id INTEGER,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES COMPILATIONS (ID),
    FOREIGN KEY (event_id) REFERENCES EVENTS (ID)
);
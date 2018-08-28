CREATE TABLE PRODUCT (ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY NOT NULL, ITEMID INTEGER, DESCRIPTION VARCHAR(255), PRICE DOUBLE, QTY DOUBLE)
CREATE TABLE PRODUCT_TO_RENT (ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY NOT NULL, PRODUCT_ID INTEGER, PRICE DOUBLE, SOFT_LIMIT INTEGER, HARD_LIMIT INTEGER, FINE DOUBLE)
CREATE TABLE RENTPRODUCT (ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY NOT NULL, PRODUCT_TO_RENT_ID INTEGER, RENT_ID INTEGER)
CREATE TABLE RENT (ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY NOT NULL, DATE DATE, TOTAL DOUBLE, STATUS CHAR(1), CLIENT_ID INTEGER)
CREATE TABLE RETURNPRODUCT (ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY NOT NULL, PRODUCT_TO_RENT_ID INTEGER, RETURN_ID INTEGER)
CREATE TABLE RETURN (ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY NOT NULL, DATE DATE, STATUS CHAR(1), CLIENT_ID INTEGER)
CREATE TABLE HISTORY (ID INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY NOT NULL, PRODUCT_ID INT, CLIENT_ID INT, STATUS CHAR(15), INSERT_DATE DATE)
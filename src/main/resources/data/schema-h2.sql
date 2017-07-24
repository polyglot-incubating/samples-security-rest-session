  
CREATE TABLE USER (
  id            INT PRIMARY KEY AUTO_INCREMENT,
  username      VARCHAR(64) NOT NULL,
  password      VARCHAR(64),  
  firstname     VARCHAR(100),
  lastname      VARCHAR(100),
  email         VARCHAR(100),
  country_code  VARCHAR(10),
  zip_code      VARCHAR(10),
  address       VARCHAR(200),
  cellphone     VARCHAR(20),
  birthdate     VARCHAR(8),
  gender        VARCHAR(1),
  enabled       SMALLINT NOT NULL DEFAULT 1,
  reg_dtm       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

alter table USER add constraint AK_USER unique( username );

CREATE TABLE PERMISSION (
  perm_cd   VARCHAR(100) PRIMARY KEY,
  descr     VARCHAR(200) NOT NULL,
  enabled   SMALLINT NOT NULL DEFAULT 1,
  reg_dtm   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE USER_PERM_REL (
  user_id    INT NOT NULL,
  perm_cd    VARCHAR(100) NOT NULL,
  reg_dtm    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE USER_PERM_REL ADD PRIMARY KEY (user_id, perm_cd);

########################################
# BangBang MySQL DB create file 
########################################
#mysql -u root -p dbname < filename.sql

########################
# Create userinfo table
########################
CREATE TABLE userinfo
(
  uid          int       NOT NULL AUTO_INCREMENT,
  nick_name    char(50)  NOT NULL ,
  pwd          char(50)  NOT NULL ,
  description  char(50)  NULL ,
  email        char(50)  NOT NULL ,
  sex          char(8)   NOT NULL ,
  age          int       NULL ,
  points       int       NULL ,
  create_at    int       NULL ,
  modify_at    int       NULL ,
  city         char(50)  NULL ,
  image        char(50)  NULL ,
  birthday     int       NULL ,
  PRIMARY KEY (uid)
) ENGINE=InnoDB;


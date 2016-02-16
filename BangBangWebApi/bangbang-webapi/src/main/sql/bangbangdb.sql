########################################
# BangBang MySQL DB create file 
########################################
/*
#1.make sure ur /etc/my.conf :
[mysqld]
character-set-server=utf8mb4
[mysql]
default-character-set=utf8mb4
#2.create db:
mysql> create database bangbangdb DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
#3.create table:
[root@localhost]#mysql -u root -p bangbangdb < filename.sql
password: <enter directly for no pwd>
#4. show chinese on terminal: mysql>set names utf8; 
*/
########################
# Create user table
########################
CREATE TABLE User
(
  uid          int       	NOT NULL AUTO_INCREMENT,
  type         int       	NULL,
  username     varchar(150)  NULL,
  password     char(255)  	NOT NULL ,
  email        char(35)    NOT NULL,
  salt         char(50)  	NULL ,
  externalId   char(50)  	NOT NULL ,

  nickName     char(50)       character set utf8mb4 collate utf8mb4_unicode_ci NULL ,
  province     int            NULL ,
  city         int            NULL ,
  description  char(255)      NULL ,
  image        char(100)      NULL ,
  gender       char(1)        NULL default "u",
  age          int            NULL ,
  birthDay     datetime       NULL default "1980-01-01 11:22:33",
  points       int            NOT NULL default 0,
  jobCompleteCount   int      NOT NULL default 0,
  jobPublishCount    int      NOT NULL default 0,
  create_at     datetime     NOT NULL,
  modify_at     datetime     NULL,
  qqId         char(20)       NULL,
  weiXinId     char(20)       NULL,  
  school       char(20)       NULL,  
  company      char(20)       NULL,
  head_photo_modify_at     datetime     NULL,
  identifyCode  char(4)           NULL,
  PRIMARY KEY (uid)
) ENGINE=InnoDB default character set utf8mb4 collate utf8mb4_unicode_ci;

########################
# Create job table
########################
CREATE TABLE Job
(
  jobId        int       	NOT NULL AUTO_INCREMENT,
  title        char(30)      character set utf8mb4 collate utf8mb4_unicode_ci NOT NULL,
  description  varchar(255)  character set utf8mb4 collate utf8mb4_unicode_ci NOT NULL,
  ownerId      int(255)  	NOT NULL,
  dueTime      datetime 	NOT NULL,
  status   	   int	  	NOT NULL,
  rewardType   int       	NOT NULL,
  reward       char(20)       NOT NULL,
  longitude    double         NOT NULL ,
  latitude     double         NOT NULL ,
  publishTime  datetime      NOT NULL ,

  pickerId       int	     NOT NULL default 1,
  pickTime       datetime    NULL ,
  pointsToOwner  int         NOT NULL default 0,
  pointsToPicker int	     NOT NULL default 0,
  completeTime   datetime    NULL,
  closeTime      datetime    NULL,
  modifyTime     datetime    NULL, 

  PRIMARY KEY (jobId)
) ENGINE=InnoDB default character set utf8mb4 collate utf8mb4_unicode_ci;
# Create Suggestion table
########################
CREATE TABLE Suggestion
(
  sid          int       	NOT NULL AUTO_INCREMENT,
  uid         int            NOT NULL,
  suggestion char(250)     character set utf8mb4 collate utf8mb4_unicode_ci NOT NULL,
  create_at     datetime     NOT NULL,
  fixed         boolean     NOT NULL default FALSE,
  comments      VARCHAR(250) character set utf8mb4 collate utf8mb4_unicode_ci NULL,
  PRIMARY KEY (sid)
) ENGINE=InnoDB default character set utf8mb4 collate utf8mb4_unicode_ci;
########################
# Create messages table
# consider emoji, changing character
########################
CREATE TABLE messages
(
  msgId          int        NOT NULL AUTO_INCREMENT,
  jobid         int            NOT NULL,
  ownerUid       int      NOT NULL,
  content        VARCHAR(250)   character set utf8mb4 collate utf8mb4_unicode_ci NULL ,
  createTime   datetime     NOT NULL,
  type           int        NULL,
  PRIMARY KEY (msgId)
) ENGINE=InnoDB default character set utf8mb4 collate utf8mb4_unicode_ci;
########################
# Create uidcidmap table for getui push 
########################
CREATE TABLE uidcidmap
(
  id             int   NOT NULL AUTO_INCREMENT,
  uid         int         NOT NULL ,
  cid        VARCHAR(60)      NOT NULL,
  modifyTime   datetime     NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB default charset=utf8;
########################
# Create common table
########################
CREATE TABLE system
(
  latestServerVersion             char(255)   NOT NULL,
  latestAndroidClientVersion      char(255)   NOT NULL,
  latestIOSClientVersion      char(255)   NOT NULL,
  latestAndroidClientName             char(255)   NOT NULL,
  latestIOSClientName             char(255)   NOT NULL
) ENGINE=InnoDB default charset=utf8;
CREATE INDEX index_uidcidtable_uid ON uidcidmap(uid);
CREATE INDEX index_job_id ON messages(jobid);
CREATE INDEX index_user_email ON User(email);
/* DROP INDEX index_job_id ON messages; */
INSERT INTO User (type, password,email,nickName) VALUES (0,"$2a$10$WlfmtSYDJi/r7Rc8cQKhpubN2eaVofS5AqbtvIEfZgE243wVQenpS", "admin@bangbang.com","administrator");
/* insert current newest version for common table */
INSERT INTO system (latestServerVersion,latestAndroidClientVersion, latestIOSClientVersion,latestAndroidClientName,latestIOSClientName) VALUES ("1.0.1","1.0.1","0","BangBangClient.apk","BangBangClient.apk");
# change character for emoji in table messages
# For each table:
# ALTER TABLE messages CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# For each column:
# ALTER TABLE messages CHANGE content content VARCHAR(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

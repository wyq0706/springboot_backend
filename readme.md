可在application.properties中修改本地mysql配置

mysql建表：
```
create database project;

use project;

create table user (id int unsigned auto_increment,
username varchar(25) not null,signature varchar(50),icon longblob,
password varchar(15) not null,personal_info varchar(200),
verification boolean default false,type boolean not null,
school varchar(30), department varchar(30),grade varchar(20),
real_name varchar(30),
primary key (id));

create table project (id int unsigned auto_increment,
title varchar(40) not null,research_direction varchar(50),
requirement varchar(80),
description varchar(200),teacher_id int unsigned,
created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
primary key (id),
foreign key (teacher_id) references user(id) on delete cascade on update cascade );

create table rela_follow (followed_id int unsigned,follower_id int unsigned);

create table rela_project_signin (project_id int unsigned,student_id int unsigned);

create table rela_project_star (project_id int unsigned,student_id int unsigned);

create table plan (id int unsigned auto_increment,
title varchar(40) not null,plan_direction varchar(50),
type varchar(20),
description varchar(200),student_id int unsigned,
created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
primary key (id),
foreign key (student_id) references user(id) on delete cascade on update cascade );

create table chat (id int unsigned auto_increment,
from_id int unsigned,to_id int unsigned,
message varchar(100) not null,
created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
primary key (id));
```
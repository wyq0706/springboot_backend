可在application.properties中修改本地mysql配置

mysql建表：
```
create database project;

use project;

create table user (id int unsigned auto_increment,
username varchar(25) not null,signature varchar(50),icon longblob,
password varchar(15) not null,personal_info varchar(200),
verification boolean default false,type boolean not null,
primary key (id));

create table project (id int unsigned auto_increment,
title varchar(40) not null,research_direction varchar(50),
requirement varchar(80),
description varchar(200),teacher_id int,
primary key (id),
foreign key (teacher_id) references user(id) on delete cascade on update cascade );
```
可在application.properties中修改本地mysql配置

mysql建表：
```
create database project;

use project;

create table student (id int auto_increment,
username varchar(25) not null,signature varchar(50),icon longblob,
password varchar(15) not null,personal_info varchar(200),
verification boolean default false,primary key (id));

create table teacher (id int auto_increment,
username varchar(25) not null,signature varchar(50),icon longblob,
password varchar(15) not null,personal_info varchar(200),
verification boolean default false,primary key (id));
```
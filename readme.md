可在application.properties中修改本地mysql配置

###mysql建表语句
```
create database project character set utf8

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

### elasticsearch配置
####依赖版本
elasticsearch-6.2.2
elasticsearch-analysis-ik-6.2.2 中文分词插件
####安装说明
* windows
    * 下载Elasticsearch6.2.2的zip包，并解压到指定目录，下载地址：https://www.elastic.co/cn/downloads/past-releases/elasticsearch-6-2-2
    * 安装中文分词插件，在elasticsearch-6.2.2\bin目录下执行以下命令：
        ```
      elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.2.2/elasticsearch-analysis-ik-6.2.2.zip
        ```
    * 运行bin目录下的elasticsearch.bat启动Elasticsearch
    * 打开http://localhost:9200看到elasticsearch的配置文字表明安装成功
    
    
####可参考学习资料
* https://juejin.im/post/5cfba3e9f265da1b614fea60#heading-9
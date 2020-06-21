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
message varchar(100) not null, ifRead varchar(1) default false,
created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
primary key (id));

create table sysInfo (id int unsigned auto_increment,
from_id int unsigned,to_id int unsigned,
message varchar(100) not null, ifRead varchar(1) default false,
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
      速度慢的话，建议离线下载插件，加压放到plugins目录中。
    * 运行bin目录下的elasticsearch.bat启动Elasticsearch
    * 打开http://localhost:9200看到elasticsearch的配置文字表明安装成功
    
* linux(ubuntu)
    * 踩坑无数后，把linux当作本地机子进行配置，也就是跟windows基本一样的操作。不一样的是，es不允许root用户跑服务，因此得创建一个新用户，给权限，再运行。详情可以看https://blog.csdn.net/ProMonkey_chen/article/details/80489000
    * 以下是不可行的方案（全是坑。。。） 强烈建议如果是自己的单个学生机服务器，不要用docker，容易爆内存，还有跨域问题，以及其他千奇百怪的问题。。。
        * 先安装docker
            ```
            curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun
            ```
        * 安装elasticsearch和ik插件
            ```
            docker pull docker.elastic.co/elasticsearch/elasticsearch:6.2.2
            docker run -d --name es  -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" [填imageID]
            // install ik plugin
            docker exec -it es /bin/bash
            ./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.2.2/elasticsearch-analysis-ik-6.2.2.zip
            exit
            docker restart es
            ```  
        https://blog.csdn.net/u012211603/article/details/90757253
####可参考学习资料
* https://juejin.im/post/5cfba3e9f265da1b614fea60#heading-9
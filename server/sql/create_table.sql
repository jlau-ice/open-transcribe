# 数据库初始化
-- 创建库
create database if not exists open_transcribe;

-- 切换库
use open_transcribe;

-- 用户表
create table if not exists user
(
    id            bigint auto_increment comment 'id' primary key,
    user_account  varchar(256)                             not null comment '账号',
    user_password varchar(512)                             not null comment '密码',
    union_id      varchar(256)                             null comment '微信开放平台id',
    mp_open_id    varchar(256)                             null comment '公众号openId',
    user_name     varchar(256)                             null comment '用户昵称',
    user_avatar   varchar(1024)                            null comment '用户头像',
    user_profile  varchar(512)                             null comment '用户简介',
    user_role     varchar(256)   default 'user'            not null comment '用户角色：user/admin/ban',
    integral      int            default 0                 not null comment '用户积分',
    amount        decimal(10, 2) default 0.00              not null comment '用户余额',
    temp1         varchar(255)                             null comment '保留字段1',
    temp2         varchar(255)                             null comment '保留字段2',
    temp3         varchar(255)                             null comment '保留字段3',
    create_time   datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint        default 0                 not null comment '是否删除',
    index idx_union_id (union_id)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 音频文件表
create table if not exists audio_file
(
    id          bigint auto_increment comment 'id' primary key,
    user_id     bigint                             not null comment '创建用户 id',
    file_name   varchar(256)                       not null comment '文件名',
    file_path   varchar(1024)                      not null comment '文件路径',
    file_size   int                                not null comment '文件大小(kb)',
    duration    int      default 0                 not null comment '音频时长',
    file_type   varchar(256)                       not null comment '文件类型(wav,mp3..)',
    status      int      default 0                 not null comment '文件状态（0 - 待处理、1 - 处理中、2 - 处理成功、3 - 处理失败）',
    temp1       varchar(255)                       null comment '保留字段1',
    temp2       varchar(255)                       null comment '保留字段2',
    temp3       varchar(255)                       null comment '保留字段3',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除'
) comment '音频文件' collate = utf8mb4_unicode_ci;


-- 转写任务表
create table if not exists transcription_task
(
    id                bigint auto_increment comment 'id' primary key,
    user_id           bigint                             not null comment '创建用户 id',
    file_id           bigint                             not null comment '音频文件 id',
    model_name        varchar(256)                       null comment '模型名称',
    interface_address varchar(1024)                      null comment '接口地址',
    execution_time    bigint                             null comment '执行时间',
    status            int      default 0                 not null comment '任务状态（0 - 待处理、1 - 处理中、2 - 处理成功、3 - 处理失败）',
    result            text                               null comment '结果',
    temp1             varchar(255)                       null comment '保留字段1',
    temp2             varchar(255)                       null comment '保留字段2',
    temp3             varchar(255)                       null comment '保留字段3',
    create_time       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete         tinyint  default 0                 not null comment '是否删除'
) comment '音频文件' collate = utf8mb4_unicode_ci;


CREATE TABLE `transcribe_result`
(
    `id`          bigint(20) NOT NULL COMMENT '主键',
    `audio_id`    bigint(20)          DEFAULT NULL COMMENT '音频id',
    `result_text` text                DEFAULT NULL COMMENT '转录结果',
    `status`      varchar(128)        DEFAULT NULL COMMENT '状态',
    `start_time`  datetime            DEFAULT NULL COMMENT '任务开始时间',
    `end_time`    datetime            DEFAULT NULL COMMENT '任务结束时间',
    `temp1`       varchar(255)        DEFAULT NULL COMMENT '保留字段1',
    `temp2`       varchar(255)        DEFAULT NULL COMMENT '保留字段2',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT ='转录结果表'
  collate = utf8mb4_unicode_ci;
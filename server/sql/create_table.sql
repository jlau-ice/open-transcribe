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

-- 题目表
create table if not exists question
(
    id           bigint auto_increment comment 'id' primary key,
    title        varchar(512)                       null comment '标题',
    content      text                               null comment '内容',
    difficulty   varchar(255)                       null comment '难度',
    tags         json                               null comment '标签列表（json 数组）',
    answer       text                               null comment '题目答案',
    submit_num   int      default 0                 not null comment '题目提交数',
    accepted_num int      default 0                 not null comment '题目通过数',
    judge_case   json                               null comment '判题用例（json 数组）',
    judge_config json                               null comment '判题配置（json 对象）',
    thumb_num    int      default 0                 not null comment '点赞数',
    favour_num   int      default 0                 not null comment '收藏数',
    user_id      bigint                             not null comment '创建用户 id',
    temp1        varchar(255)                       null comment '保留字段1',
    temp2        varchar(255)                       null comment '保留字段2',
    temp3        varchar(255)                       null comment '保留字段3',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (user_id)
) comment '题目' collate = utf8mb4_unicode_ci;


-- 题目提交表
create table if not exists question_submit
(
    id               bigint auto_increment comment 'id' primary key,
    question_id      bigint                             not null comment '题目 id',
    user_id          bigint                             not null comment '创建用户 id',
    language         varchar(128)                       not null comment '编程语言',
    code             text                               not null comment '用户代码',
    judge_info       json                               null comment '判题信息（json 对象）执行时间等等',
    judgment_results text                               null comment '判题结果枚举值 ',
    status           int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    temp1            varchar(255)                       null comment '保留字段1',
    temp2            varchar(255)                       null comment '保留字段2',
    temp3            varchar(255)                       null comment '保留字段3',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete        tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (question_id),
    index idx_userId (user_id)
) comment '题目提交';
-- 帖子表
create table if not exists post
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    thumb_num   int      default 0                 not null comment '点赞数',
    favour_num  int      default 0                 not null comment '收藏数',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    index idx_user_id (user_id)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id          bigint auto_increment comment 'id' primary key,
    post_id     bigint                             not null comment '帖子 id',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_post_id (post_id),
    index idx_user_id (user_id)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id          bigint auto_increment comment 'id' primary key,
    post_id     bigint                             not null comment '帖子 id',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_post_id (post_id),
    index idx_user_id (user_id)
) comment '帖子收藏';

create table hibernate_sequence
(
    next_val bigint not null
);

create table refesh_token
(
    id           bigint      not null
        primary key,
    created_date varchar(45) null,
    token        varchar(45) null
);

create table user
(
    user_id  bigint       not null
        primary key,
    created  datetime     null,
    email    varchar(255) null,
    enabled  bit          null,
    username varchar(255) not null,
    password varchar(255) not null
);

create table subreddit
(
    id           bigint       not null
        primary key,
    created_date datetime     null,
    description  varchar(255) not null,
    name         varchar(55)  not null,
    user_user_id bigint       null,
    constraint fk_subreddit_user
        foreign key (user_user_id) references user (user_id)
);

create table post
(
    post_id      bigint       not null
        primary key,
    created_date datetime     null comment '		',
    description  longtext     null,
    url          varchar(255) null,
    post_name    varchar(255) not null,
    vote_count   int          null,
    id           bigint       null,
    user_id      bigint       null,
    constraint fk_post_subreddit
        foreign key (id) references subreddit (id),
    constraint fk_post_user
        foreign key (user_id) references user (user_id)
);

create table comment
(
    id           bigint       not null
        primary key,
    created_date datetime     null,
    text         varchar(255) not null,
    post_id      bigint       null,
    user_id      bigint       null,
    constraint fk_comment_post
        foreign key (post_id) references post (post_id),
    constraint fk_comment_user
        foreign key (user_id) references user (user_id)
);

create index fk_comment_post_idx
    on comment (post_id);

create index fk_comment_user_idx
    on comment (user_id);

create index fk_post_subreddit_idx
    on post (id);

create index fk_post_user_idx
    on post (user_id);

create index fk_subreddit_user_idx
    on subreddit (user_user_id);

create table subreddit_posts
(
    subreddit_id  bigint null,
    posts_post_id bigint null,
    constraint UK_ih17w4fa2em7w3u1tt8gqv2wh
        unique (posts_post_id),
    constraint FK1plpyiqs72shw84g90q0fes5r
        foreign key (subreddit_id) references subreddit (id),
    constraint FKl27wc8sin3rt45ayge7fanx10
        foreign key (posts_post_id) references post (post_id)
);

create table token
(
    id           bigint auto_increment
        primary key,
    expiry_date  varchar(45) null,
    token        varchar(45) null,
    user_user_id bigint      null,
    constraint fk_token_user
        foreign key (user_user_id) references user (user_id)
);

create index fk_token_user_idx
    on token (user_user_id);

create table user_comments
(
    user_user_id bigint null,
    comments_id  bigint null
);

create table user_posts
(
    user_user_id  int         null,
    posts_post_id varchar(45) null
);

create table user_subreddits
(
    user_user_id  bigint      null,
    subreddits_id varchar(45) null,
    constraint fk_user_subreddits_user
        foreign key (user_user_id) references user (user_id)
);

create index fk_user_subreddits_user_idx
    on user_subreddits (user_user_id);

create table vote
(
    vote_id   bigint      not null
        primary key,
    vote_type varchar(45) null,
    post_id   bigint      not null,
    user_id   bigint      null,
    constraint fk_vote_post
        foreign key (post_id) references post (post_id),
    constraint fk_vote_user
        foreign key (user_id) references user (user_id)
);

create index fk_vote_post_idx
    on vote (post_id);

create index fk_vote_user_idx
    on vote (user_id);


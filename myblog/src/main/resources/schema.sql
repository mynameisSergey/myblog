create table if not exists post(
    id bigint auto_increment primary key,
    title varchar(250) not null,
    image blob,
    text varchar(4000),
    tags varchar(4000),
    likes_count integer default 0
);

create table if not exists comment(
    id bigint auto_increment primary key,
    comment_text varchar(4000),
    post_id bigint not null,
    foreign key (post_id) references post(id) on delete cascade
);

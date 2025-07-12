-- Таблица постов
CREATE TABLE if not exists posts
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY, -- id поста
    title       VARCHAR(250)  NOT NULL,            -- Наименование поста
    text        VARCHAR(4000) NOT NULL,            -- Текст поста
    tags        VARCHAR(4000),                     -- Теги
    likes_count INTEGER DEFAULT 0,                 -- Лайки
    image       BLOB                               -- Картинка
);

-- Таблица комментариев
CREATE TABLE if not exists comments
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY, -- id комментария
    post_id BIGINT        NOT NULL,            -- id поста
    text    VARCHAR(4000) NOT NULL,            -- Текст комментария
    FOREIGN KEY (post_id) REFERENCES posts (id)


);

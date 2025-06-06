CREATE TABLE user_pp
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    profile_pic_url VARCHAR(255),
    CONSTRAINT fk_user_pp_user FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE TABLE IF NOT EXISTS groups (
  uuid UUID PRIMARY KEY,
  name varchar(200) NOT NULL,
  visible_member_list boolean NOT NULL DEFAULT false,
  anonymous_vote boolean NOT NULL DEFAULT false
);
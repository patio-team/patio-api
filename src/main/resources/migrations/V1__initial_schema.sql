CREATE TABLE IF NOT EXISTS groups (
  id UUID PRIMARY KEY,
  name varchar(200) NOT NULL,
  visible_member_list boolean NOT NULL DEFAULT false,
  anonymous_vote boolean NOT NULL DEFAULT false
);
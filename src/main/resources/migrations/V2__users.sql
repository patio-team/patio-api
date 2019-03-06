CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY,
  name varchar(200) NOT NULL,
  email varchar(200) NOT NULL,
  password varchar(200) NOT NULL,
  otp varchar(200) NULL
);


CREATE TABLE IF NOT EXISTS users_groups (
  user_id UUID,
  group_id UUID,
  is_admin boolean NOT NULL DEFAULT false,
  CONSTRAINT PK_Users_Groups PRIMARY KEY (user_id, group_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (group_id) REFERENCES groups(id)
);
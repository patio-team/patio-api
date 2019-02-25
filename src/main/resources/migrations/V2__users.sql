CREATE TABLE IF NOT EXISTS users (
  uuid UUID PRIMARY KEY,
  name varchar(200) NOT NULL,
  email varchar(200) NOT NULL,
  password varchar(200) NOT NULL,
  otp varchar(200) NULL
);


CREATE TABLE IF NOT EXISTS users_groups (
  user_uuid UUID,
  group_uuid UUID,
  is_admin boolean NOT NULL DEFAULT false,
  CONSTRAINT PK_Users_Groups PRIMARY KEY (user_uuid, group_uuid),
  FOREIGN KEY (user_uuid) REFERENCES users(uuid),
  FOREIGN KEY (group_uuid) REFERENCES groups(uuid)
);
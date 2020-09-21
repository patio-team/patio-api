--
-- Copyright (C) 2019 Kaleidos Open Source SL
--
-- This file is part of PATIO.
-- PATIO is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- PATIO is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with PATIO.  If not, see <https://www.gnu.org/licenses/>
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS notifications (
  id UUID NOT NULL PRIMARY KEY,
  type varchar(200) NOT NULL,
  is_active Boolean NOT NULL DEFAULT true,
  notifying_time time with time zone NULL,
  user_id UUID NOT NULL,
  group_id UUID NULL,
  FOREIGN KEY(group_id) REFERENCES "groups"(id) ON DELETE CASCADE,
  FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO notifications (id, type, is_active, user_id, group_id)
    SELECT
      uuid_generate_v4(),
      'POLL_START',
      true,
      user_id,
      group_id
    FROM users_groups;

ALTER TABLE users_groups
  ADD COLUMN poll_notification_id UUID NULL,
  ADD CONSTRAINT poll_notification
  FOREIGN KEY (poll_notification_id) REFERENCES notifications(id);

UPDATE users_groups ug
SET (poll_notification_id) = (
    SELECT n.id
	FROM notifications n
		WHERE n.group_id = ug.group_id
		AND n.user_id = ug.user_id );



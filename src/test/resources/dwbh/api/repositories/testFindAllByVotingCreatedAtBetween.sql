--
-- Copyright (C) 2019 Kaleidos Open Source SL
--
-- This file is part of Don't Worry Be Happy (DWBH).
-- DWBH is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- DWBH is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with DWBH.  If not, see <https://www.gnu.org/licenses/>
--

INSERT INTO users (id, name, email, password, otp) VALUES ('486590a3-fcc1-4657-a9ed-5f0f95dadea6','Sue Storm', 'sstorm@email.com', 'password', '');
INSERT INTO groups (id, name, visible_member_list, anonymous_vote, voting_time, voting_days) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','Fantastic Four', false, true, time with time zone '10:48:12.146512+01:00', '{"MONDAY"}');
INSERT INTO groups (id, name, visible_member_list, anonymous_vote, voting_time, voting_days) VALUES ('d64db962-3455-11e9-b210-d663bd873d94','Avengers', false, true, time with time zone '11:48:12.146512+01:00', '{"MONDAY"}');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'f');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d94','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 'f');
INSERT INTO voting (id, group_id, created_at, created_by, average) VALUES ('7772e35c-5a87-4ba3-ab93-da8a957037fd', 'd64db962-3455-11e9-b210-d663bd873d93', '2020-05-04T10:15:30+01:00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', 3);
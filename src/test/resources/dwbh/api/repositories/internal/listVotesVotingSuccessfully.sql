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

INSERT INTO groups (id, name, voting_time, voting_days) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','Fantastic Four', time with time zone '14:48:12.146512+01:00', '{"MONDAY"}');
INSERT INTO users (id, name, email, password, otp) VALUES ('486590a3-fcc1-4657-a9ed-5f0f95dadea6','Sue Storm', 'sstorm@email.com', 'password', '');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 't');
INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('ffad4562-4971-11e9-98cd-d663bd873d93', 'd64db962-3455-11e9-b210-d663bd873d93', now(), '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('8b4508b7-df83-47e1-9a26-ff799c000559', 'ffad4562-4971-11e9-98cd-d663bd873d93', now(), '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 4);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('63c647bb-6dbf-4a3b-9da8-1e16ff7cfdb8', 'ffad4562-4971-11e9-98cd-d663bd873d93', now(), '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 5);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('e73bfe9a-aa74-4d67-aa18-e5097efea6e3', 'ffad4562-4971-11e9-98cd-d663bd873d93', now(), '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 5);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('98ee1697-d4ff-41ea-9309-7cd6f5f87fcf', 'ffad4562-4971-11e9-98cd-d663bd873d93', now(), '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 5);















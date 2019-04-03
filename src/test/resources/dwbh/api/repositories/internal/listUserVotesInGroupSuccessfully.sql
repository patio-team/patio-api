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

-- two groups
INSERT INTO groups (id, name, voting_time, voting_days) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','Fantastic Four', time with time zone '14:48:12.146512+00:00', '{"MONDAY"}');
INSERT INTO groups (id, name, voting_time, voting_days) VALUES ('dedc6675-ab79-495e-9245-1fc20545eb83', 'Avengers', time with time zone '14:48:12.146512+00:00', '{"MONDAY"}');

-- two users
INSERT INTO users (id, name, email, password, otp) VALUES ('486590a3-fcc1-4657-a9ed-5f0f95dadea6','Sue Storm', 'sstorm@email.com', 'password', '');
INSERT INTO users (id, name, email, password, otp) VALUES ('c2a771bc-f8c5-4112-a440-c80fa4c8e382','Ben Grim', 'bgrim@email.com', 'password', '');

-- both users belong to Fantastic Four
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 't');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','c2a771bc-f8c5-4112-a440-c80fa4c8e382', 't');

-- Sue belong also to avengers
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('dedc6675-ab79-495e-9245-1fc20545eb83','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 't');

-- Fantastic four has four votings
-- In the first voting, Sue and Ben vote
INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('ffad4562-4971-11e9-98cd-d663bd873d93', 'd64db962-3455-11e9-b210-d663bd873d93', '2019-01-01 00:00:00+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('8b4508b7-df83-47e1-9a26-ff799c000559', 'ffad4562-4971-11e9-98cd-d663bd873d93', '2019-01-01 00:00:10+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 4);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('0805d4f7-2419-44d7-a2a2-a23c9390988b', 'ffad4562-4971-11e9-98cd-d663bd873d93', '2019-01-01 00:00:10+00', 'c2a771bc-f8c5-4112-a440-c80fa4c8e382', '', 4);

-- In the second voting, nobody votes
INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('333b8e1a-350a-4fbb-8831-384d0ccdfd1c', 'd64db962-3455-11e9-b210-d663bd873d93', '2019-01-02 00:00:00+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');


-- In the third voting, only Ben vote
INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('088fe60c-9d7d-42eb-806e-c828b5a9c933', 'd64db962-3455-11e9-b210-d663bd873d93', '2019-01-03 00:00:00+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('6c63cafa-1fe7-4bab-ba05-c8dc38dbdf54', '088fe60c-9d7d-42eb-806e-c828b5a9c933', '2019-01-03 00:00:10+00', 'c2a771bc-f8c5-4112-a440-c80fa4c8e382', '', 4);

-- In the fourth voting, only Sue vote
INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('fb6723e9-8bcd-4fb8-abb0-e08f51b384bf', 'd64db962-3455-11e9-b210-d663bd873d93', '2019-01-04 00:00:00+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('b69a496b-51b5-4bdd-9f68-83984a5ee626', 'fb6723e9-8bcd-4fb8-abb0-e08f51b384bf', '2019-01-04 00:00:10+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 4);


-- Avengers has five votings, and sue votes on all
INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('157cf433-a653-44ac-ab10-5a8f72367048', 'dedc6675-ab79-495e-9245-1fc20545eb83', '2019-01-01 00:00:00+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('35dd1e58-ac20-4d47-b499-f2f7dbe09fbe', '157cf433-a653-44ac-ab10-5a8f72367048', '2019-01-01 00:00:10+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 4);

INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('a3164236-3543-4008-b773-854bb94fd328', 'dedc6675-ab79-495e-9245-1fc20545eb83', '2019-01-02 00:00:00+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('67756c71-5ea2-4d0e-9eb2-78553dc1971a', 'a3164236-3543-4008-b773-854bb94fd328', '2019-01-02 00:00:10+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 4);

INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('508dc274-5934-4dbc-abba-6d03e3c285fe', 'dedc6675-ab79-495e-9245-1fc20545eb83', '2019-01-03 00:00:00+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('6a8e0f58-82a0-44bf-aded-5c9dbeaf8be4', '508dc274-5934-4dbc-abba-6d03e3c285fe', '2019-01-03 00:00:10+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 4);

INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('b23ab920-78ed-417b-a9fe-ae803eaf79af', 'dedc6675-ab79-495e-9245-1fc20545eb83', '2019-01-04 00:00:00+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('313b234a-ea8a-49a7-b991-a24caa608cf1', 'b23ab920-78ed-417b-a9fe-ae803eaf79af', '2019-01-04 00:00:10+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 4);

INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('5d719c63-542c-4527-a4ac-0aabfc0d0112', 'dedc6675-ab79-495e-9245-1fc20545eb83', '2019-01-05 00:00:00+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6');
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('fe38a3b1-8f0a-4732-a017-88c1432c2168', '5d719c63-542c-4527-a4ac-0aabfc0d0112', '2019-01-05 00:00:10+00', '486590a3-fcc1-4657-a9ed-5f0f95dadea6', '', 4);


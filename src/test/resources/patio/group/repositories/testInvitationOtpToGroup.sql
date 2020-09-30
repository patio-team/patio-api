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

-- Avengers
INSERT INTO groups (id, name, anonymous_vote, voting_time, voting_days, voting_duration) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','Avengers', true, time with time zone '00:00:00.146512+01:00', '{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"}', 24);
INSERT INTO users (id, name, email, password, otp) VALUES ('486590a3-fcc1-4657-a9ed-5f0f95dadea6','Sue Storm', 'sstorm@email.com', 'password', '');
INSERT INTO users (id, name, email, password, otp) VALUES ('c2a771bc-f8c5-4112-a440-c80fa4c8e382','Ben Grim', 'bgrim@email.com', 'password', '');
INSERT INTO users (id, name, email, password, otp) VALUES ('84d48a35-7659-4710-ad13-4c47785a0e9d','Johnny Storm', 'jstorm@email.com', 'password', '');
INSERT INTO users (id, name, email, password, otp) VALUES ('1998c588-d93b-4db6-92e2-a9dbb4cf03b5','Steve Rogers', 'srogers@email.com', 'password', '');
INSERT INTO users (id, name, email, password, otp) VALUES ('3465094c-5545-4007-a7bc-da2b1a88d9dc','Tony Stark', 'tstark@email.com', 'password', '');
INSERT INTO users (id, name, email, password, otp) VALUES ('3465094c-5545-4007-a7bc-da2b1a88d9dd', null, 'newUser@email.com', null, null);
-- Avengers's members
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','486590a3-fcc1-4657-a9ed-5f0f95dadea6', 't');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','c2a771bc-f8c5-4112-a440-c80fa4c8e382', 'f');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','1998c588-d93b-4db6-92e2-a9dbb4cf03b5', 'f');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','84d48a35-7659-4710-ad13-4c47785a0e9d', 'f');
-- invited users to join the Avengers
INSERT INTO users_groups (group_id, user_id, is_acceptance_pending, invitation_otp, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','3465094c-5545-4007-a7bc-da2b1a88d9dc', 't', '$1Otp/a2b1a88d9d1', 'f');
INSERT INTO users_groups (group_id, user_id, is_acceptance_pending, invitation_otp, is_admin) VALUES ('d64db962-3455-11e9-b210-d663bd873d93','3465094c-5545-4007-a7bc-da2b1a88d9dd', 't', '$2Otp/a2b1a88d9d2', 'f');

-- Another Team
INSERT INTO groups (id, name, anonymous_vote, voting_time, voting_days, voting_duration) VALUES ('dd4db962-3455-11e9-b210-d663bd873d94','Another Team', true, time with time zone '00:00:00.146512+01:00', '{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"}', 24);
INSERT INTO users (id, name, email, password, otp) VALUES ('1118c588-d93b-4db6-92e2-a9dbb4cf0111','hero', 'hero@email.com', 'password', '');
INSERT INTO users_groups (group_id, user_id, is_admin) VALUES ('dd4db962-3455-11e9-b210-d663bd873d94','1118c588-d93b-4db6-92e2-a9dbb4cf0111', 'f');
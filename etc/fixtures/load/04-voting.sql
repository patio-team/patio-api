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

INSERT INTO voting (id, group_id, created_at, created_by) VALUES ('ffad4562-4971-11e9-98cd-d663bd873d93', 'dedc6675-ab79-495e-9245-1fc20545eb83', now(), '3465094c-5545-4007-a7bc-da2b1a88d9dc');
INSERT INTO voting (id, group_id, created_at, created_by, average) VALUES ('8690a94a-499d-11e9-8646-d663bd873d93', 'dedc6675-ab79-495e-9245-1fc20545eb83', '2019-01-01', '3465094c-5545-4007-a7bc-da2b1a88d9dc',4);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('59ccd8e4-cde5-4e1b-b971-729d4311c7d0', '8690a94a-499d-11e9-8646-d663bd873d93', '2019-01-01', '3465094c-5545-4007-a7bc-da2b1a88d9dc', 'Happy :)', 5);
INSERT INTO vote (id, voting_id, created_at, created_by, comment, score) VALUES ('36156d7e-c5f1-40c5-a105-06cd9563127c', '8690a94a-499d-11e9-8646-d663bd873d93', '2019-01-01', '06259dfb-8610-40e2-b81c-4f4483fad3dd', 'Meh', 3);



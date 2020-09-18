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

ALTER TABLE users_groups
    ADD COLUMN IF NOT EXISTS is_acceptance_pending boolean NULL default FALSE,
    ADD COLUMN IF NOT EXISTS invitation_otp varchar(200) NULL,
    ADD COLUMN IF NOT EXISTS otp_creation_date timestamp with time zone NULL,
    ADD COLUMN IF NOT EXISTS member_from_date timestamp with time zone NULL,
    ADD COLUMN IF NOT EXISTS inviting_id UUID NULL,
    ADD CONSTRAINT inviting_id FOREIGN KEY (inviting_id) REFERENCES users(id);


ALTER TABLE fk_reim_main
    ADD COLUMN version INT NOT NULL DEFAULT 0 AFTER owner_user_id;

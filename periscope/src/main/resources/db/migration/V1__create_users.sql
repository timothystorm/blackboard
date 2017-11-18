CREATE TABLE periscope.users (
  -- identity
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username   VARCHAR(256)    NOT NULL UNIQUE,

  -- security
  password   VARCHAR(256)    NOT NULL,
  salt       CHAR(32)        NOT NULL
  COMMENT 'key stretching',
  iter       INTEGER         NOT NULL DEFAULT 10000
  COMMENT 'subsequent hashes',

  -- status
  enabled    BOOLEAN         NOT NULL DEFAULT TRUE,
  expired    BOOLEAN         NOT NULL DEFAULT FALSE,
  locked     BOOLEAN         NOT NULL DEFAULT FALSE,

  -- audit
  created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME        NULL,

  -- keys and constraints
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = UTF8;

-- indexes and triggers
DELIMITER $$
CREATE TRIGGER trig_users_bu
BEFORE UPDATE ON periscope.users
FOR EACH ROW
  BEGIN
    SET NEW.updated_at := now();
  END;
$$
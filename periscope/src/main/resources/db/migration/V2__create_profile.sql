CREATE TABLE periscope.profile (
  -- identity
  id          BIGINT UNSIGNED NOT NULL
  COMMENT 'FK:users.id',

  -- name
  family_name VARCHAR(64)     NULL,
  given_name  VARCHAR(64)     NULL,

  -- details
  email       VARCHAR(128)    NULL,
  birth_date  DATE            NULL,

  -- keys and constraints
  FOREIGN KEY (id) REFERENCES periscope.users (id)
    ON DELETE CASCADE
)
  DEFAULT CHARSET = UTF8;

-- indexes and triggers
CREATE INDEX idx_profile_email ON periscope.profile(email);
CREATE INDEX idx_profile_birth_date ON periscope.profile(birth_date);
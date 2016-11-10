DROP TABLE IF EXISTS configurations;

-- removes flyway tracking table
DROP TABLE IF EXISTS schema_version;

CREATE TABLE configurations (
	id BIGSERIAL PRIMARY KEY,
	scope VARCHAR(128) NOT NULL DEFAULT 'ALL',
	key VARCHAR(128) NOT NULL,
	value VARCHAR(255) NOT NULL,
	created TIMESTAMP WITHOUT TIME ZONE DEFAULT (now() AT TIME ZONE 'UTC'),
	description VARCHAR(255)
);
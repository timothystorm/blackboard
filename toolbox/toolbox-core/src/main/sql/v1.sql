DROP TABLE IF EXISTS properties;

CREATE TABLE properties (
	"id" BIGSERIAL PRIMARY KEY,
	"key" VARCHAR(64) NOT NULL,
	"value" VARCHAR(512) NOT NULL,
	"created_at" TIMESTAMP WITHOUT TIME ZONE DEFAULT (now() AT TIME ZONE 'utc')
);
CREATE UNIQUE INDEX ON properties ((lower(key)));
CREATE INDEX ON properties ((lower(value)));

DROP TABLE IF EXISTS configuration;

CREATE TABLE configuration (
	"id" BIGSERIAL PRIMARY KEY,
	"key" VARCHAR(64) NOT NULL,
	"value" VARCHAR(512) NOT NULL,
	"created_at" TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
CREATE INDEX ON configuration ((lower(key)));
CREATE INDEX ON configuration ((lower(value)));

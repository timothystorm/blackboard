/* User login and identity attributes */
CREATE TABLE periscope.users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(256) NOT NULL UNIQUE,
  password VARCHAR(256) NOT NULL,
  salt CHAR(64) NOT NULL,
  created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME NULL,

  PRIMARY KEY (id)
)DEFAULT CHARSET=UTF8;

DELIMITER $$

CREATE TRIGGER users_before_update_trig BEFORE UPDATE ON periscope.users
  FOR EACH ROW BEGIN
    SET NEW.modified := now();
END; $$
INSERT INTO configurations(scope, key, value) VALUES('dev', 'date.independence', '1776-07-04');
INSERT INTO configurations(scope, key, value) VALUES('dev', 'url.address', 'http://google.com');
INSERT INTO configurations(scope, key, value) VALUES('dev', 'int.retries', '10');
INSERT INTO configurations(scope, key, value) VALUES('dev', 'string.message', 'hello world');
INSERT INTO configurations(scope, key, value) VALUES('dev', 'array.values', 'this|that|another');
INSERT INTO configurations(scope, key, value) VALUES('dev', 'intorpolated.value', 'only $' || '{int.retries} retries allowed');
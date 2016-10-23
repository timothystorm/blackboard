INSERT INTO configurations(scope, key, value) VALUES('papyrus', 'project.name', '0.0.0');
INSERT INTO configurations(scope, key, value) VALUES('papyrus', 'project.group', 'org.storm');
INSERT INTO configurations(scope, key, value) VALUES('papyrus', 'project.version', 'papyrus-core');
INSERT INTO configurations(scope, key, value) VALUES('papyrus', 'project.info', '$' || '{project.name}:$' || '{project.group}:$' || '{project.version}');
INSERT INTO configurations(scope, key, value) VALUES('papyrus', 'list.test', 'one|two|three');
INSERT INTO configurations(scope, key, value) VALUES('papyrus', 'escape.test', 'one\|two\|three');

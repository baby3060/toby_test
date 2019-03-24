/*
insert into sqlmap(key_, sql_) values ('add', 'Insert Into USER(id, name, password, level, login, recommend, email) Values (?, ?, ?, ?, ?, ?, ?)');
insert into sqlmap(key_, sql_) values ('deleteAll', 'Delete From USER');
insert into sqlmap(key_, sql_) values ('update', 'Update USER set name = ?, password = ?, level = ?, login = ?, recommend = ?, email = ? Where id = ?');
insert into sqlmap(key_, sql_) values ('delete', 'Delete From USER Where id = ?');
insert into sqlmap(key_, sql_) values ('get', 'Delete From USER Where id = ?');
insert into sqlmap(key_, sql_) values ('count', 'Select * From USER Where id = ?');
insert into sqlmap(key_, sql_) values ('countAll', 'Select Count(*) As cnt From USER');
insert into sqlmap(key_, sql_) values ('selectAll', 'Select * From USER Order By id');
*/

delete From sqlmap;

commit;
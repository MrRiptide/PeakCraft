use peakcraft;
insert into `items` select * from `new_items`;
delete from `new_items` A where a.id in (select B.id from `items` B);
select * from `new_items`;
select * from `items`;
################################
#  create Job for test
################################
delimiter //
create procedure createjob()
begin 
    declare i int;
    set i=0;

    while i<=10000 do
        insert into Job (title, description, ownerId, status,longitude,latitude) values (concat("robot job",i),concat("robot job description",i),7,1,121.40875,31.107587);
        set i=i+1;
    end while;
end
//
delimiter ;
# notice the spcae before ;

#then mysql> call createjob();

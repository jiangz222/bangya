################################
#  create user for test
################################
delimiter //
create procedure createuser1()
begin 
    declare i int;
    set i=0;

    while i<=10 do
        insert into User (type, password, email, nickName) values (0,"afdsfasdfdsafdsfdsfasdfdsafasdfdsfdsfasdf",concat("fdsaf",i,"@.bangbang.com"),concat("iam robot",i));
        set i=i+1;
    end while;
end
//
delimiter ;
# notice the space before ;

#then mysql> call createuser();

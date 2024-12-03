-- drop table d_dining;
create table d_dining (
  id serial not null
  , days date
  , room_no text
  , customer_num integer
  , dinner text
  , dinner_time text
  , dinner_place text
  , breakfast_japanese integer
  , breakfast_western integer
  , breakfast_time text
  , breakfast_place text
  , lunch_num integer
  , lunch_time text
  , memo text
  , primary key (id)
  , UNIQUE (days, room_no)
);


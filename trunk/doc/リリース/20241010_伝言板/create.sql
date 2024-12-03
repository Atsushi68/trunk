-- drop table d_dining_message;
create table d_dining_message (
  days date
  , dining_place_id integer
  , message text
  , primary key (days, dining_place_id)
);


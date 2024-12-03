--------------------------------------------------
-- 田本
--------------------------------------------------
-- 管理者ログイン
--
drop table if exists m_admin;
create table m_admin (
  admin_id text not null,
  password text,
  role text,
  admin_name text,
  del_flg boolean,
  last_date timestamp,
  last_user text,
  primary key (admin_id)
);

-- pass : q7XvW4Ti
insert into m_admin(admin_id,password,"role",admin_name,del_flg,last_date,last_user) values ('ADMIN','$2a$10$nX6v4FRHMNu0Do1ogmy8ze7jjWRM8mL3XA77bi6dWl1jDQbp4QjSS','ROLE_ADMIN','ADMINユーザ',False,null,null);

-- ユーザーログイン
--
drop table if exists m_user;
create table m_user (
  room_no text not null,
  token text,
  from_dt timestamp,
  to_dt timestamp,
  action_name text, -- 新規発行、再発行、削除
  del_flg boolean,
  last_date timestamp,
  last_user text
);

-- お土産注文
-- IDが同じでも金額・商品名が違う場合は別商品扱いにする想定
drop view if exists vd_cart;
drop table if exists t_cart;
drop table if exists d_cart;
create table d_cart (
  cart_id serial not null,
  token text,               -- m_user.token
  omiyage_id int not null,  -- カート追加時のお土産ID
  price integer,            -- カート追加時の金額
  num integer,              -- 購入個数
  cart_token text,          -- 複数タブ対応の為のユニークな値
  del_flg boolean,
  last_date timestamp,
  last_user text,
  primary key (cart_id)
);
drop table if exists t_cart_lang;
drop table if exists d_cart_lang;
create table d_cart_lang (
  cart_id int not null,
  language text, -- 言語(ja,en,zh-tw,zh-cn,ko)
  name text,     -- カート追加時の商品名
  primary key (cart_id, language)
);
create or replace view vd_cart as
select
c.cart_id,
c.token,
c.omiyage_id,
c.price,
c.num,
c.cart_token,
c.del_flg,
c.last_date,
c.last_user,
cl.language,
cl.name
from 
d_cart c
inner join d_cart_lang cl on c.cart_id = cl.cart_id;


drop table if exists t_order;
drop table if exists d_order;
create table d_order (
  order_id serial not null,
  token text,               -- m_user.token
  omiyage_id int not null,  -- 購入時のm_omiyage.omiyage_id
  price integer,            -- 購入時のm_omiyage.price
  num integer,              -- 購入個数
  del_flg boolean,
  last_date timestamp,
  last_user text,
  primary key (order_id)
);

drop table if exists t_order_lang;
drop table if exists d_order_lang;
create table d_order_lang (
  order_id int not null,
  language text, -- 言語(ja,en,zh-tw,zh-cn,ko)
  name text,     -- 購入時の商品名
  primary key (order_id, language)
);

drop view if exists vd_order;
create or replace view vd_order as
select
o.order_id,
o.token,
o.omiyage_id,
o.price,
o.num,
o.del_flg,
o.last_date,
o.last_user,
ol.language,
ol.name
from 
d_order o
inner join d_order_lang ol on o.order_id = ol.order_id;

--------------------------------------------------
-- 藤原
--------------------------------------------------
-- お土産マスタ
--
drop view if exists vm_omiyage;
drop table if exists m_omiyage;
create table m_omiyage (
  omiyage_id serial not null,
  category text,
  image_ext text,
  price integer,
  sort_omomi integer,
  del_flg boolean,
  last_date timestamp,
  last_user text,
  primary key (omiyage_id)
);
drop table if exists m_omiyage_lang;
create table m_omiyage_lang (
  omiyage_id int not null,
  language text, -- 言語(ja,en,zh-tw,zh-cn,ko)
  tags text,
  name text,
  detail text,
  amount_product text,
  raw_materials text,
  allergie text,
  primary key (omiyage_id, language)
);
create or replace view vm_omiyage as
  select
    m.omiyage_id,
    m.category,
    m.image_ext,
    m.price,
    m.sort_omomi,
    m.del_flg,
    ml.language,
    ml.tags,
    ml.name,
    ml.detail,
    ml.amount_product,
    ml.raw_materials,
    ml.allergie,
    m.last_date,
    m.last_user
  from
    m_omiyage m
    inner join m_omiyage_lang ml on m.omiyage_id = ml.omiyage_id
;

drop table if exists d_reserve_bath;
create table d_reserve_bath (
  reserve_date text, -- yyyyMMdd
  time_cd text,      -- 時間帯コード
  bath_cd text,      -- 風呂コード
  room_no text,      -- 部屋番号
  del_flg boolean,
  last_date timestamp,
  last_user text
);

drop table if exists m_time;
create table m_time (
  time_cd text,        -- 時間帯コード
  disp_text text,      -- 表示用文字列 15:00～15:45
  selectable boolean, -- 選択可能
  next_day boolean,   -- 次日
  hm_from text,        -- 範囲From HHmm 0～12時は +24時表記
  hm_to text           -- 範囲From HHmm 0～12時は +24時表記
);

insert into m_time values ('01', '',             false, false, '1200', '1500');
insert into m_time values ('02', '15:00～15:45', true,  false, '1500', '1600');
insert into m_time values ('03', '16:00～16:45', true,  false, '1600', '1700');
insert into m_time values ('04', '17:00～17:45', true,  false, '1700', '1800');
insert into m_time values ('05', '18:00～18:45', true,  false, '1800', '1900');
insert into m_time values ('06', '19:00～19:45', true,  false, '1900', '2000');
insert into m_time values ('07', '20:00～20:45', true,  false, '2000', '2100');
insert into m_time values ('08', '21:00～21:45', true,  false, '2100', '2200');
insert into m_time values ('09', '22:00～22:45', true,  false, '2200', '2300');
insert into m_time values ('10', '23:00～23:45', true,  false, '2300', '2400');
insert into m_time values ('11', '',             false, true,  '2400', '3030');
insert into m_time values ('12', '6:30～7:15',   true,  true,  '3030', '3130');
insert into m_time values ('13', '7:30～8:15',   true,  true,  '3130', '3230');
insert into m_time values ('14', '8:30～9:15',   true,  true,  '3230', '3330');
insert into m_time values ('15', '9:30～10:15',  true,  true,  '3330', '3430');
insert into m_time values ('16', '',             false, true,  '3430', '3600');

drop table if exists m_room;
create table m_room (
  room_no text,    -- 部屋番号
  floor integer   -- 階
);

insert into m_room values ('201', 2);
insert into m_room values ('202', 2);
insert into m_room values ('203', 2);
insert into m_room values ('206', 2);
insert into m_room values ('207', 2);
insert into m_room values ('208', 2);
insert into m_room values ('209', 2);
insert into m_room values ('301', 3);
insert into m_room values ('302', 3);
insert into m_room values ('303', 3);
insert into m_room values ('304', 3);
insert into m_room values ('305', 3);
insert into m_room values ('306', 3);
insert into m_room values ('307', 3);
insert into m_room values ('308', 3);
insert into m_room values ('311', 3);
insert into m_room values ('401', 4);
insert into m_room values ('402', 4);
insert into m_room values ('403', 4);
insert into m_room values ('404', 4);
insert into m_room values ('411', 4);
insert into m_room values ('501', 5);
insert into m_room values ('502', 5);
insert into m_room values ('503', 5);
insert into m_room values ('504', 5);
insert into m_room values ('601', 6);
insert into m_room values ('602', 6);
insert into m_room values ('603', 6);
insert into m_room values ('604', 6);


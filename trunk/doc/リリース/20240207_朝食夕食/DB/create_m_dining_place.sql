-- drop table m_dining_place;
create table m_dining_place (
  id serial not null
  , kbn integer
  , dinner_place text
  , sort_order integer
  , del_flg boolean
  , primary key (id)
);
insert into m_dining_place
(kbn, dinner_place, sort_order, del_flg)
values
(1, '睦月', 1, false),
(1, '如月', 2, false),
(1, '弥生', 3, false),
(1, '卯月', 4, false),
(1, '皐月', 5, false),
(1, '水無月', 6, false),
(1, '文月', 7, false),
(1, '葉月', 8, false),
(1, '長月', 9, false),
(1, '神無月', 10, false),
(1, '霜月', 11, false),
(1, '師走', 12, false),

(2, '松葉', 13, false),
(2, '山桜', 14, false),
(2, '菖蒲', 15, false),
(2, '露草', 16, false),
(2, '紅梅', 17, false),
(2, '山吹', 18, false),
(2, '若竹', 19, false),
(2, '千草', 20, false),
(2, '牡丹', 21, false);

drop table m_time;
create table m_time (
  time_cd text
  , disp_text text
  , bath_type text
  , selectable boolean
  , next_day boolean
  , hm_from text
  , hm_to text
);

insert into taketori.m_time(time_cd, disp_text, bath_type, selectable, next_day, hm_from, hm_to) values 
    ('01','','',False,False,'1100','1430')
  , ('02','14:30�`15:15','�I�V���C',True,False,'1430','1515')
  , ('03','15:00�`15:45','�Ƒ����C',True,False,'1500','1600')
  , ('04','15:30�`16:15','�I�V���C',True,False,'1530','1615')
  , ('05','16:00�`16:45','�Ƒ����C',True,False,'1600','1700')
  , ('06','16:30�`17:15','�I�V���C',True,False,'1630','1715')
  , ('07','17:00�`17:45','�Ƒ����C',True,False,'1700','1800')
  , ('08','17:30�`18:15','�I�V���C',True,False,'1730','1815')
  , ('09','18:00�`18:45','�Ƒ����C',True,False,'1800','1900')
  , ('10','18:30�`19:15','�I�V���C',True,False,'1830','1915')
  , ('11','19:00�`19:45','�Ƒ����C',True,False,'1900','2000')
  , ('12','19:30�`20:15','�I�V���C',True,False,'1930','2015')
  , ('13','20:00�`20:45','�Ƒ����C',True,False,'2000','2100')
  , ('14','20:30�`21:15','�I�V���C',True,False,'2030','2115')
  , ('15','21:00�`21:45','�Ƒ����C',True,False,'2100','2200')
  , ('16','21:30�`22:15','�I�V���C',True,False,'2130','2215')
  , ('17','22:00�`22:45','�Ƒ����C',True,False,'2200','2300')
  , ('18','22:30�`23:15','�I�V���C',True,False,'2230','2315')
  , ('19','23:00�`23:45','�Ƒ����C',True,False,'2300','2400')
  , ('20','23:30�`24:15','�I�V���C',True,False,'2330','2415')
  , ('21','','',False,True,'2415','3000')
  , ('22','6:00�`6:45','�I�V���C',True,True,'3000','3045')
  , ('23','6:30�`7:15','�Ƒ����C',True,True,'3030','3130')
  , ('24','7:00�`7:45','�I�V���C',True,True,'3100','3145')
  , ('25','7:30�`8:15','�Ƒ����C',True,True,'3130','3230')
  , ('26','8:00�`8:45','�I�V���C',True,True,'3200','3245')
  , ('27','8:30�`9:15','�Ƒ����C',True,True,'3230','3330')
  , ('28','9:00�`9:45','�I�V���C',True,True,'3300','3345')
  , ('29','9:30�`10:15','�Ƒ����C',True,True,'3330','3430')
  , ('30','10:00�`10:45','�I�V���C',True,True,'3400','3445')
  , ('31','','',False,True,'3445','3600');

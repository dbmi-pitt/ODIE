drop table if exists word_pair_scores ;

create table word_pair_scores as select
   a.i_x_y ixy,
   a.freq fxy,
   h1.freq fx,   
   h1.word x,
   h2.freq fy,   
   h2.word y
from
   church_analysis4.word_histogram h1,
   church_analysis4.word_histogram h2,
   church_analysis4.word_association a
where
   a.word_one_id = h1.id and
   a.word_two_id = h2.id and
   length(h1.word) > 2 and
   length(h2.word) > 2 and
   a.freq > 2
order by
   a.i_x_y desc limit 1000 ;
   
alter table word_pair_scores add id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST ;
   
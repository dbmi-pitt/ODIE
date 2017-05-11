select
   a.i_x_y ixy,
   a.freq fxy,
   h1.freq fx,   
   h1.word x,
   h2.freq fy,   
   h2.word y
from
   church_analysis2.ne_histogram h1,
   church_analysis2.ne_histogram h2,
   church_analysis2.ne_association a
where
   a.word_one_id = h1.id and
   a.word_two_id = h2.id and
   length(h1.word) > 2 and
   length(h2.word) > 2 and
   a.freq > 2
order by
   a.i_x_y desc limit 1000 ;
   
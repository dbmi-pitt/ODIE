select 
   word_one, 
   word_two, 
   nscore_church, 
   nscore_lin,
   nscore_lsp
from 
    combined_word_pair 
where 
    word_one != word_two and
    length(word_one) > 0 and 
    length(word_two) > 0
order by 
   nscore_lsp desc, 
   word_one, 
   word_two 
limit 50
INTO OUTFILE 'C:/workspace/ws-uima-tutorial/IndexFinder/csvfiles/917/best_lsp.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n' ;
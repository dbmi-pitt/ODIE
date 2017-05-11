select 
   word_one, 
   word_two, 
   nscore_church, 
   nscore_lin, 
   nscore_lsp 
from 
    combined_word_pair 
where 
    nscore_church >= 0 and
    nscore_lin >= 0 and
    nscore_lsp >= 0
order by 
   nscore_church desc, 
   word_one, 
   word_two 
limit 50
INTO OUTFILE 'C:/workspace/ws-uima-tutorial/IndexFinder/csvfiles/917/combined.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

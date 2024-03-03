alter table nfts_catalog
    add column family  enum ('ITALIAN','YAKUZA') DEFAULT 'ITALIAN';

alter table nfts_catalog
    add column roi varchar(255) DEFAULT '0%';

update nfts_catalog set roi = '120% - 145%' where name = 'Soldato' and id = 2;
update nfts_catalog set roi = '122% - 147%' where name = 'Avvocato' and id = 3;
update nfts_catalog set roi = '124% - 148%' where name = 'Consigliere' and id = 4;
update nfts_catalog set roi = '126% - 151%' where name = 'Sottocapo' and id = 5;
update nfts_catalog set roi = '128% - 153%' where name = 'Regina' and id = 6;
update nfts_catalog set roi = '130% - 156%' where name = 'Capo' and id = 7;
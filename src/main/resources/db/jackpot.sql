create table jackpots (
  id bigint not null auto_increment,
  created datetime(6) not null,
  base_entry_price decimal(19,5) not null,
  current_amount decimal(19,5) not null,
  last_updated datetime(6) not null,
  status enum ('CLOSED','ONGOING'),
  version integer not null,
  primary key (id)
);

create table jackpots_history (
  id bigint not null auto_increment,
  created datetime(6) not null,
  amount_won decimal(19,5) not null,
  roll_number integer,
  soldatonft bigint,
  timestamp datetime(6) not null,
  user_id bigint not null,
  winning_category_id bigint not null,
  primary key (id)
);

alter table transactions
    modify column transaction_type  enum ('DEPOSIT','DEVELOPER_FEE_WITHDRAW','GIFT_BNB','JACKPOT_ENTRY','JACKPOT_NOTHING','JACKPOT_SOLDATO','JACKPOT_WIN','JACKPOT_X10','JACKPOT_X1_POINT_5','JACKPOT_X2','JACKPOT_X3','JACKPOT_X5','MINT_NFT_BNB','MINT_NFT_BNB_FEE','MINT_NFT_MCOIN','NFT_REWARD','PLAY_STRONGBOX','WIN_STRONGBOX','WITHDRAW');

create table winning_categories (
    id bigint not null auto_increment,
    created datetime(6) not null,
    range_end integer not null,
    range_start integer not null,
    reward_type enum ('JACKPOT','NOTHING','SOLDATO','X10','X1point5','X2','X3','X5'),
    winning_chance varchar(255) not null,
    primary key (id)
);

alter table jackpots_history
    add constraint FK60hs4qyxq5k654lpgbw0ys8ls
        foreign key (user_id)
            references _user (id);

alter table jackpots_history
    add constraint FK9cyeacs2v5ixh1wtqrdet9aue
        foreign key (winning_category_id)
            references winning_categories (id);
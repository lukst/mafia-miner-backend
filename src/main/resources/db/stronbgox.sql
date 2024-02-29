create table strongbox_combinations (
    id bigint not null auto_increment,
    created datetime(6) not null,
    is_attempted bit not null,
    number integer not null,
    game_id bigint,
    primary key (id)
);

create table strongbox_game (
    id bigint not null auto_increment,
    created datetime(6) not null,
    attempt_cost decimal(19,3) not null,
    attempts_count integer not null,
    current_multiplier integer not null,
    last_reset_time datetime(6) not null,
    version integer not null,
    winning_combination integer not null,
    primary key (id)
);

create table strongbox_history (
    id bigint not null auto_increment,
    created datetime(6) not null,
    win_amount decimal(19,5) not null,
    win_time datetime(6) not null,
    winning_combination integer not null,
    winning_multiplier integer not null,
    game_id bigint not null,
    user_id bigint,
    primary key (id)
);

alter table strongbox_combinations
    add constraint FKimobr8y0oqcklcef5wixqolqa
        foreign key (game_id)
            references strongbox_game (id);

alter table strongbox_history
    add constraint FKjqpo4nwc9m6936f8j0nq7fbtq
        foreign key (game_id)
            references strongbox_game (id);

alter table strongbox_history
    add constraint FKgekfwuh9mw56vii5lp2e3tddn
        foreign key (user_id)
            references _user (id);
drop table if exists New_V_FundIO_449;

create table New_V_FundIO_449
(
    id           int            not null auto_increment,
    num          decimal(10, 2) not null,
    from_user_id int            not null,
    PRIMARY KEY (id)
);

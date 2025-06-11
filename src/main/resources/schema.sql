-- Hibernate: 
    create table airport (
        id bigint not null auto_increment,
        city varchar(100) not null,
        code varchar(10) not null,
        name varchar(100) not null,
        primary key (id)
    ) engine=InnoDB
-- Hibernate: 
    create table booking (
        id bigint not null auto_increment,
        booking_time datetime(6) not null,
        reference varchar(20) not null,
        status varchar(20) not null,
        total_price decimal(10,2) not null,
        flight_id bigint not null,
        user_id bigint not null,
        primary key (id)
    ) engine=InnoDB
-- Hibernate: 
    create table flight (
        id bigint not null auto_increment,
        departure_date date not null,
        departure_time time(6) not null,
        flight_number varchar(10) not null,
        price decimal(10,2) not null,
        departure_airport_id bigint not null,
        destination_airport_id bigint not null,
        primary key (id)
    ) engine=InnoDB
-- Hibernate: 
    create table passenger (
        id bigint not null auto_increment,
        email varchar(100) not null,
        first_name varchar(50) not null,
        last_name varchar(50) not null,
        booking_id bigint not null,
        primary key (id)
    ) engine=InnoDB
-- Hibernate:
    create table user (
        id bigint not null auto_increment,
        country varchar(100) not null,
        email varchar(100) not null,
        first_name varchar(50) not null,
        last_name varchar(50) not null,
        password varchar(255) not null,
        phone varchar(20) not null,
        primary key (id)
    ) engine=InnoDB
-- Hibernate: 
    alter table booking
       add constraint FK546eybei9q7dsna94vryofrbr
       foreign key (flight_id)
       references flight (id)
-- Hibernate: 
    alter table booking
       add constraint FKkgseyy7t56x7lkjgu3wah5s3t
       foreign key (user_id)
       references user (id)
-- Hibernate: 
    alter table flight
       add constraint FKillsy04237nltbk2yryrbderb
       foreign key (departure_airport_id)
       references airport (id)
-- Hibernate: 
    alter table flight
       add constraint FK6uc5h994cl1g7yxsvnxkilqbl
       foreign key (destination_airport_id)
       references airport (id)
-- Hibernate: 
    alter table passenger
       add constraint FKtco0omesfld1qi5sw76eomvt4
       foreign key (booking_id)
       references booking (id)
--회원관리
create table spmember(
 mem_num number not null,
 id varchar2(12) unique not null,
 nick_name varchar2(30),
 auth number(1) default 2 not null, --0:탈퇴회원,1:정지회원,2:일반회원,9:관리자
 constraint spmember_pk primary key (mem_num)
);

create table spmember_detail(
 mem_num number not null,
 au_id varchar2(36) unique, --자동로그인 시 들어갈 식별자(첫 입력 시 null이기 때문에 null 허용)
 name varchar2(30) not null,
 passwd varchar2(35) not null,
 phone varchar2(15) not null,
 email varchar2(50) not null,
 zipcode varchar2(5) not null,
 address1 varchar2(90) not null,
 address2 varchar2(90) not null,
 photo blob,
 photo_name varchar2(100),
 reg_date date default sysdate not null,
 modify_date date,
 constraint spmember_detail_pk primary key (mem_num),
 constraint spmember_detail_fk foreign key (mem_num) references spmember (mem_num)
);

create sequence spmember_seq; 

--게시판
create table spboard(
 board_num number not null,
 title varchar2(90) not null,
 content clob not null,
 hit number(8) default 0 not null,
 reg_date date default sysdate not null,
 modify_date date,
 filename varchar2(200),
 ip varchar2(40) not null,
 mem_num number not null,
 constraint spboard_pk primary key (board_num),
 constraint spboard_fk foreign key (mem_num) references spmember (mem_num)
);

create sequence spboard_seq;

--게시판 좋아요
create table spboard_fav(
 board_num number not null,
 mem_num number not null,
 constraint fav_spboard_fk1 foreign key (board_num) references spboard (board_num),
 constraint fav_spmember_fk2 foreign key (mem_num) references spmember (mem_num)
);












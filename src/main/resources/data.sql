insert into member
(email,is_deleted,member_name,password,role)
values
    ('admin@naver.com',0,'admin123','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER');

insert into member
(email,is_deleted,member_name,password,role)
values
    ('test1@naver.com',0,'test1','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER');
insert into member
(email,is_deleted,member_name,password,role)
values
    ('test2@naver.com',0,'test2','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER');
insert into member
(email,is_deleted,member_name,password,role)
values

insert into member
(email,is_deleted,member_name,password,role)
values
    ('test4@naver.com',0,'test4','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER');
insert into member
(email,is_deleted,member_name,password,role)
values
    ('test5@naver.com',0,'test5','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER');
insert into member
(email,is_deleted,member_name,password,role)
values
    ('test6@naver.com',0,'test6','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER');
insert into member
(email,is_deleted,member_name,password,role)
values
    ('test7@naver.com',0,'test7','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER');


insert into member_game_info
(is_observer, exp, member_game_info_id, member_id, in_game_role, level, nickname, profile_img)
values
    (0,'0','1','1','NONE','BEGINNER','admin','PROFILE01');
insert into member_game_info
(is_observer, exp, member_game_info_id, member_id, in_game_role, level, nickname, profile_img)
values
    (0,'0','2','2','NONE','BEGINNER','test1','PROFILE01');
insert into member_game_info
(is_observer, exp, member_game_info_id, member_id, in_game_role, level, nickname, profile_img)
values
    (0,'0','3','3','NONE','BEGINNER','test3','PROFILE01');
insert into member_game_info
(is_observer, exp, member_game_info_id, member_id, in_game_role, level, nickname, profile_img)
values
    (0,'0','4','4','NONE','BEGINNER','test4','PROFILE01');
insert into member_game_info
(is_observer, exp, member_game_info_id, member_id, in_game_role, level, nickname, profile_img)
values
    (0,'0','5','5','NONE','BEGINNER','test5','PROFILE01');
insert into member_game_info
(is_observer, exp, member_game_info_id, member_id, in_game_role, level, nickname, profile_img)
values
    (0,'0','6','6','NONE','BEGINNER','test6','PROFILE01');
insert into member_game_info
(is_observer, exp, member_game_info_id, member_id, in_game_role, level, nickname, profile_img)
values
    (0,'0','7','7','NONE','BEGINNER','test7','PROFILE01');


insert into chat_room
(close, capacity, chatroom_id, head, owner_id, password, state, title)
values
    (0, '7', '1', '0', '1', '', 'WAITING', 'mafia');
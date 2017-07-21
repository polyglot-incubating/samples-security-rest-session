-- noinspection SqlNoDataSourceInspectionForFile

-- noinspection SqlDialectInspectionForFile

INSERT INTO USER (id, username, password) VALUES
  (801001, 'usr1@daum.net', 'qwer!234'),
  (801002, 'usr2@naver.com', 'qwer!234'),
  (801003, 'usr.fabok@facebook.com', 'qwer!234'),
  (100001, 'lamp.java@gmail.com', 'qwer!234'),
  (100002, 'abc@abc', 'qwer!234');

INSERT INTO PERMISSION (perm_cd, descr, enabled) VALUES
  ('API_ComCode.add', '공통', 1),
  ('API_ComCode.get', '공통', 1),
  ('API_ComCode.query', '공통', 1),
  ('API_ComCode.modify', '공통', 1),
  ('API_ComCode.remove', '공통', 1),
  ('API_ComCode.enable', '공통', 1),
  ('API_ComCode.disable', '공통', 1),
  ('PORTAL_Usr.get', '포탈', 1),
  ('PORTAL_Usr.query', '포탈', 1);

INSERT INTO USER_PERM_REL(user_id, perm_cd) VALUES
  ( 100001, 'API_ComCode.add' ),
  ( 100001, 'API_ComCode.get' ),
  ( 100001, 'API_ComCode.query' ),
  ( 100001, 'API_ComCode.modify' ),
  ( 100001, 'API_ComCode.remove' ),
  ( 100001, 'API_ComCode.enable' ),
  ( 100001, 'API_ComCode.disable' ),
  ( 100001, 'PORTAL_Usr.get' ),
  ( 100001, 'PORTAL_Usr.query' );

INSERT INTO USER_PERM_REL(user_id, perm_cd) VALUES
  ( 100002, 'PORTAL_Usr.get' ),
  ( 100002, 'PORTAL_Usr.query' );
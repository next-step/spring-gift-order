INSERT INTO roles (name) VALUES
     ('ROLE_USER'),
     ('ROLE_MD'),
     ('ROLE_ADMIN');

-- test 어드민 사용자 test@test.com qwerty1234@
INSERT INTO users (email, password, client_id, provider) VALUES
     ('test@test.com', '1469f57c482317fba59bb34d16c10b0f5116e64c2201e430a70cc16a34a6a785', NULL, 'EMAIL'),
     (null, null, 'd4a76f830d09f0064d77af2700955dc600dd7d1b3c9ba3ecc44a99fbb94ef54f', 'KAKAO');

-- 관리자 계정 부여
INSERT INTO user_roles(user_id, role_name) VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER');
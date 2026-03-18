USE studentdb;
UPDATE users SET password_hash = '$2a$10$bmGGy/aoq9M4YI5gsHldzuW/z24NUANEvWV4fNwa5gNapNq7GcMeC' WHERE username = 'admin';
UPDATE users SET password_hash = '$2a$10$dAK1/tMYesuh6suyjdVztes6l5M8CpjkH.AHp3HpMVod2w1xxDZpm' WHERE username = 'teacher1';
UPDATE users SET password_hash = '$2a$10$anY9hw91kja5TetnGEiJHea7ZJESJES.ses2alGiCvzjc4Na.FNJy' WHERE username = 'student1';

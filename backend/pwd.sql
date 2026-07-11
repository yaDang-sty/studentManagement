UPDATE student SET password = RIGHT(phone, 6) WHERE phone IS NOT NULL AND phone != '';
UPDATE student SET password = '123456' WHERE password IS NULL OR password = '';

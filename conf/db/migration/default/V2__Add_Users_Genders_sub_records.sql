-- Insert genders info
INSERT INTO public.genders
(gender_id, gender_name, ord)
VALUES
  ('id_female', 'Female', 0),
  ('id_male', 'Male', 1),
  ('id_other', 'Other', 2);

-- Insert users info
INSERT INTO public.users
(first_name, last_name, email, mobile_number, image, password, salt, gender_id)
VALUES
  ('Nischal', 'Basnet', 'nischal@outlook.com', '444-333-4234', 'nischal.jpg', 'password', 'salt', 'id_male'),
  ('Niluja', 'Singh', 'niluja@outlook.com', '444-324-1124', 'niluja.png', 'password', 'salt', 'id_female'),
  ('Ram', 'Thapa', 'ram@outlook.com', '444-324-1122', 'ram.png', 'password', 'salt', 'id_male'),
  ('Hari', 'Man', 'hari@outlook.com', '444-324-2233', 'hari.png', 'password', 'salt', 'id_male'),
  ('Sita', 'Gautam', 'sita@outlook.com', '444-324-3322', 'sita.png', 'password', 'salt', 'id_female'),
  ('Shanthi', 'Singh', 'shanthi@outlook.com', '444-223-1124', 'shanthi.png', 'password', 'salt', 'id_female'),
  ('Swarup', 'Pradhan', 'spradhan@outlook.com', '334-324-1124', 'swarup.png', 'password', 'salt', 'id_male'),
  ('Mina', 'Takada', 'mtak@outlook.com', '423-324-1124', 'mina.png', 'password', 'salt', 'id_female'),
  ('Jack', 'Johnes', 'jj@outlook.com', '554-324-1124', 'jack.png', 'password', 'salt', 'id_male'),
  ('Bruce', 'Lee', 'lee@outlook.com', '998-324-1124', 'bruce.png', 'password', 'salt', 'id_male');
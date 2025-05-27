--
-- PresentIdea
--
INSERT INTO public.presentidea (importance, id, listid, description, name, url)
VALUES (1, 1, 1, 'A handcrafted star map of the night sky on a specific birthday date.', 'Personalized Star Map',
        'https://example.com/star-map'),
       (5, 2, 1, 'Premium over-ear headphones for immersive audio experiences.', 'Wireless Noise-Canceling Headphones',
        'https://example.com/headphones'),
       (4, 3, 1, 'A sleek smartwatch that monitors heart rate, sleep, and fitness goals.',
        'Smartwatch with Health Tracking', 'https://example.com/smartwatch'),
       (3, 4, 1, 'A gourmet cooking experience at a local restaurant.', 'Cooking Class for Two',
        'https://example.com/cooking-class'),
       (2, 5, 2, 'A modern desk lamp with built-in wireless charging and adjustable brightness.',
        'Wireless Charging Desk Lamp', 'https://example.com/lamp');
--
--  WishList Data
--
INSERT INTO public.wishlist (active, expires, id, description, name, username)
VALUES ('t', 1779888096, 1,
        'A list of unforgettable experiences and adventures desired for the birthday celebration, such as travel vouchers, workshops, or special events!',
        'Birthday Experience Wishlist', 'dummyUser'),
       ('t', 1779888096, 2,
        'A collection of thoughtful gift ideas and presents desired for the birthday celebration, including both tangible items and memorable experiences!',
        'Birthday Gift Wishlist', 'dummyUser');
--
--  Sequence alter
--
alter sequence presentidea_seq restart with 6;
alter sequence wishlist_seq restart with 3;
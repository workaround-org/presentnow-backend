--
-- PresentIdea
--
INSERT INTO public.presentidea (importance, id, listid, description, name, url, claimed, claimername)
VALUES (1, '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'A handcrafted star map of the night sky on a specific birthday date.', 'Personalized Star Map',
        'https://example.com/star-map', false, null),
       (5, '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'Premium over-ear headphones for immersive audio experiences.',
        'Wireless Noise-Canceling Headphones',
        'https://example.com/headphones', false, null),
       (4, '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'A sleek smartwatch that monitors heart rate, sleep, and fitness goals.',
        'Smartwatch with Health Tracking', 'https://example.com/smartwatch', false, null),
       (3, '44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'A gourmet cooking experience at a local restaurant.', 'Cooking Class for Two',
        'https://example.com/cooking-class', false, null),
       (2, '55555555-5555-5555-5555-555555555555', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        'A modern desk lamp with built-in wireless charging and adjustable brightness.',
        'Wireless Charging Desk Lamp', 'https://example.com/lamp', false, null);
--
--  WishList Data
--
INSERT INTO public.wishlist (active, expires, id, description, name, username)
VALUES ('t', 1779888096, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'A list of unforgettable experiences and adventures desired for the birthday celebration, such as travel vouchers, workshops, or special events!',
        'Birthday Experience Wishlist', 'test-user'),
       ('t', 1779888096, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        'A collection of thoughtful gift ideas and presents desired for the birthday celebration, including both tangible items and memorable experiences!',
        'Birthday Gift Wishlist', 'test-user');
--
--  MagicLinkToken (seeded for MagicLinkFlowIT: hash of raw token 'native-it-magic-token-4XxUvxDbFyJ9')
--
INSERT INTO public.magiclinktoken (id, email, tokenhash, createdat, expiresat, used)
VALUES ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'seeded-it@example.com',
        '136df862d01530e5af0dd1e8c9655ad4878ffbbd52cb353ca16f4ab2a13e6d45', 0, 32503680000000, false);
--
--  Sequence alter
--
-- alter sequence presentidea_seq restart with 6;
-- alter sequence wishlist_seq restart with 3;
INSERT INTO Photographer(id, name) VALUES (1, 'Test Photographer');
INSERT INTO Photographer(id, name) VALUES (2, 'Enri');
INSERT INTO Photographer(id, name) VALUES (3, 'Steve');

INSERT INTO Journey(id, name, start, end) VALUES (1, 'United States', '2000-09-11 00:00:00.0','2006-09-11 00:00:00.0' );
INSERT INTO Journey(id, name, start, end) VALUES (2, 'Other', '2009-09-11 00:00:00.0','2015-09-11 00:00:00.0' );

INSERT INTO Place(id, city, country, latitude, longitude) VALUES (1, 'Unkown place', 'Unknown place', 0.0, 0.0);

INSERT INTO Tag(id, name) VALUES (1, 'Person');
INSERT INTO Tag(id, name) VALUES (2, 'Essen');
INSERT INTO tag(id, name) VALUES (3, 'Natur');

INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id) VALUES (3, 1,	'3.jpg', 0,	'2005-09-11 00:00:00.0', 39.7, -104.9, 1, 0);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id) VALUES (5, 1,	'5.jpg', 0,	'2015-03-04 00:00:00.0', 12.0, 12.0, 1, 0);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id) VALUES (2, 1,	'2.jpg', 0,	'2005-09-11 00:00:00.0', 39.7, -104.9, 1, 0);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id) VALUES (1, 1,	'1.jpg', 0,	'2015-03-06 00:00:00.0', 41.5, 19.5, 1, 0);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id, journey_id) VALUES (4, 1,	'4.jpg', 0,	'2005-09-11 00:00:00.0', 39.7, -104.9, 1, 0);

INSERT INTO Slideshow(id, name, durationBetweenPhotos) VALUES (1, 'Testshow', 5.0);
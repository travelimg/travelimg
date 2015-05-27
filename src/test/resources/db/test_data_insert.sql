INSERT INTO Photographer(id, name) VALUES (1, 'Test Photographer');
INSERT INTO Photographer(id, name) VALUES (2, 'Enri');
INSERT INTO Photographer(id, name) VALUES (3, 'Steve');

INSERT INTO Place(id, city, country) VALUES (1, 'Unkown place', 'Unknown place');

INSERT INTO Tag(id, name) VALUES (1, 'Person');
INSERT INTO Tag(id, name) VALUES (2, 'Essen');

INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (1, 1,	'$DIR/2005/09/11/3.jpg', 0,	'2005-09-13 00:00:00.0', 39.7, -104.9, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (2, 1,	'$DIR/2015/03/04/5.jpg', 0,	'2015-03-04 00:00:00.0', 41.9, 19.0, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (3, 1,	'$DIR/2005/09/11/2.jpg', 0,	'2005-09-12 00:00:00.0', 39.7, -104.9, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (4, 1,	'$DIR/2015/03/06/1.jpg', 0,	'2015-03-05 00:00:00.0', 41.5, 19.5, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (5, 1,	'$DIR/2015/03/06/1.jpg', 0,	'2015-03-06 00:00:00.0', 41.5, 19.5, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (6, 1,	'$DIR/2015/03/06/1.jpg', 0,	'2015-03-07 00:00:00.0', 41.5, 19.5, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (7, 1,	'$DIR/2005/09/11/4.jpg', 0,	'2005-09-11 00:00:00.0', 39.5, -104.5, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (8, 1,	'$DIR/2005/09/11/4.jpg', 0,	'2006-10-05 00:00:00.0', 39.5, 94.1, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (9, 1,	'$DIR/2005/09/11/4.jpg', 0,	'2006-10-04 00:00:00.0', 39.5, 94.6, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (10, 1,	'$DIR/2005/09/11/4.jpg', 0,	'2006-10-03 00:00:00.0', 39.5, 94.5, 1);
INSERT INTO Photo(id, photographer_id, path, rating, datetime, latitude, longitude, place_id) VALUES (11, 1,	'$DIR/2005/09/11/4.jpg', 0,	'2006-10-03 00:00:00.0', 39.03, 125.73, 1);
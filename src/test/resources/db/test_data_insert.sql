INSERT INTO Photographer(id, name) VALUES (1, 'Test Photographer');

INSERT INTO Tag(id, name) VALUES (1, 'Person');
INSERT INTO Tag(id, name) VALUES (2, 'Essen');

INSERT INTO Photo(id, photographer_id, path, rating, date, latitude, longitude) VALUES (3, 1,	'$DIR/2005/09/11/3.jpg', 0,	'2005-09-11 00:00:00.0', 39.7, -104.9);
INSERT INTO Photo(id, photographer_id, path, rating, date, latitude, longitude) VALUES (5, 1,	'$DIR/2015/03/04/5.jpg', 0,	'2015-03-04 00:00:00.0', 12.0, 12.0);
INSERT INTO Photo(id, photographer_id, path, rating, date, latitude, longitude) VALUES (2, 1,	'$DIR/2005/09/11/2.jpg', 0,	'2005-09-11 00:00:00.0', 39.7, -104.9);
INSERT INTO Photo(id, photographer_id, path, rating, date, latitude, longitude) VALUES (1, 1,	'$DIR/2015/03/06/1.jpg', 0,	'2015-03-06 00:00:00.0', 41.5, 19.5);
INSERT INTO Photo(id, photographer_id, path, rating, date, latitude, longitude) VALUES (4, 1,	'$DIR/2005/09/11/4.jpg', 0,	'2005-09-11 00:00:00.0', 39.7, -104.9);
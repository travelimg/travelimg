INSERT INTO Photographer (id, name)
SELECT 1, 'Unknown photgrapher'
  WHERE NOT EXISTS (SELECT id, name from Photographer where id = 1);
INSERT INTO Photographer (id, name)
SELECT 2, 'Flickr'
  WHERE NOT EXISTS (SELECT id, name from Photographer where id = 2);

INSERT  INTO PLACE (id, country, city, latitude, longitude, journey_id)
SELECT 1, 'Unknown place', 'Unknown place', 0.0, 0.0, 0
  WHERE NOT EXISTS (SELECT id, country, city, latitude, longitude, journey_id from Place where id = 1);

MERGE INTO Tag(id, name) VALUES (1, 'Person');
MERGE INTO Tag(id, name) VALUES (2, 'Essen');
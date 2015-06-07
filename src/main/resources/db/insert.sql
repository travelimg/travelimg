INSERT INTO Photographer (id, name)
SELECT 1, 'Unknown photgrapher'
  WHERE NOT EXISTS (SELECT id, name from Photographer where id = 1);
INSERT INTO Photographer (id, name)
SELECT 2, 'Flickr'
  WHERE NOT EXISTS (SELECT id, name from Photographer where id = 2);

MERGE INTO Tag(id, name) VALUES (1, 'Person');
MERGE INTO Tag(id, name) VALUES (2, 'Essen');
-- Sync sequence for sector.id with existing data
SELECT setval(
               pg_get_serial_sequence('sector', 'id'),
               (SELECT COALESCE(MAX(id), 1) FROM sector)
       );

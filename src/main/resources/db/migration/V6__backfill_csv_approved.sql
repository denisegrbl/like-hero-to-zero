UPDATE country_emissions
SET status = 'APPROVED'
WHERE (status IS NULL OR status = '')
  AND (created_by IS NULL OR created_by = '');

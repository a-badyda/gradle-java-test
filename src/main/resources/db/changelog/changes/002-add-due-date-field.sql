-- really the migration should be smoother, setting the field properly depending on task status etc but we'll skip that
ALTER TABLE cases
  ADD due_date TIMESTAMPTZ NOT NULL default now();

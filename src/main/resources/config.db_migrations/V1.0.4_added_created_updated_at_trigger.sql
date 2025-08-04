CREATE OR REPLACE FUNCTION new_update_modified_at()
    RETURNS trigger AS $new_update_modified_at$
BEGIN
    NEW.updated_at := (EXTRACT(epoch FROM now()) * 1000::numeric);
    RETURN NEW;
END;
$new_update_modified_at$ LANGUAGE plpgsql;

CREATE TRIGGER updateModified_At
    BEFORE INSERT OR UPDATE ON "payment_system".payment_details
    FOR EACH ROW EXECUTE PROCEDURE new_update_modified_at();

CREATE TRIGGER updateModified_At
    BEFORE INSERT OR UPDATE ON "payment_system".audit_trail
    FOR EACH ROW EXECUTE PROCEDURE new_update_modified_at();

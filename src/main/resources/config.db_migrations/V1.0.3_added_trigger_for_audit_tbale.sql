CREATE OR REPLACE FUNCTION payment_system.log_audit_trail()
RETURNS TRIGGER AS $$
DECLARE
    new_revision BIGINT;
    diff_json JSONB;
BEGIN
    IF TG_OP = 'INSERT' THEN
        NEW.revision_count := 1;

        INSERT INTO payment_system.audit_trail (
            payment_id, revision_count, revision_type,
            amount, currency, payment_type, category, user_name,
            status, created_at, updated_at
        ) VALUES (
            NEW.payment_id, NEW.revision_count, 'CREATE',
            NEW.amount, NEW.currency, NEW.payment_type, NEW.category, NEW.user_name,
            NEW.status, NEW.created_at, NEW.updated_at
        );

        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        SELECT revision_count + 1 INTO new_revision
        FROM payment_system.payment_details
        WHERE payment_id = OLD.payment_ID
        FOR UPDATE;

        NEW.revision_count := new_revision;

        INSERT INTO payment_system.audit_trail (
            payment_id, revision_count, revision_type,
            amount, currency, payment_type, category, user_name,
            status, created_at, updated_at
        ) VALUES (
             NEW.payment_id, new_revision, 'UPDATE',
            NEW.amount, NEW.currency, NEW.payment_type, NEW.category, NEW.user_name,
            NEW.status, (EXTRACT(epoch FROM now()) * 1000::numeric), NEW.updated_at
        );

        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO payment_system.audit_trail (
             payment_id, revision_count, revision_type,
            amount, currency, payment_type, category, user_name,
            status, created_at, updated_at
        ) VALUES (
             OLD.payment_id, OLD.revision_count, 'DELETE',
            OLD.amount, OLD.currency, OLD.payment_type, OLD.category, OLD.user_name,
            OLD.status, (EXTRACT(epoch FROM now()) * 1000::numeric), OLD.updated_at
        );

        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER logPaymentAudit_trigger
    BEFORE INSERT OR UPDATE OR DELETE ON "payment_system".payment_details
    FOR EACH ROW EXECUTE FUNCTION payment_system.log_audit_trail();

-- Create function to calculate formation duration (converting minutes to hours)
CREATE OR REPLACE FUNCTION calculate_formation_duration()
    RETURNS TRIGGER AS $$
DECLARE
    formation_id_val INT;
    total_minutes DECIMAL(10,1);
BEGIN
    -- Determine which formation to update
    IF TG_OP = 'DELETE' THEN
        -- Get formation_id from the module connected to the deleted support/quiz
        IF TG_TABLE_NAME = 'supports' THEN
            SELECT m.formation_id INTO formation_id_val
            FROM modules m
            WHERE m.id = OLD.module_id;
        ELSE -- quizs table
            SELECT m.formation_id INTO formation_id_val
            FROM modules m
            WHERE m.id = OLD.module_id;
        END IF;
    ELSE -- INSERT or UPDATE
    -- Get formation_id from the module connected to the new/updated support/quiz
        IF TG_TABLE_NAME = 'supports' THEN
            SELECT m.formation_id INTO formation_id_val
            FROM modules m
            WHERE m.id = NEW.module_id;
        ELSE -- quizs table
            SELECT m.formation_id INTO formation_id_val
            FROM modules m
            WHERE m.id = NEW.module_id;
        END IF;
    END IF;

    -- Calculate total minutes across all supports and quizs
    SELECT COALESCE(
                   (
                       SELECT SUM(s.duree)
                       FROM supports s
                                JOIN modules m ON s.module_id = m.id
                       WHERE m.formation_id = formation_id_val
                   ), 0) +
           COALESCE(
                   (
                       SELECT SUM(q.duree)
                       FROM quizs q
                                JOIN modules m ON q.module_id = m.id
                       WHERE m.formation_id = formation_id_val
                   ), 0)
    INTO total_minutes;

    -- Update formation duration (converting minutes to hours)
    UPDATE formations f
    SET duree = ROUND((total_minutes / 60)::numeric, 1)
    WHERE f.id = formation_id_val;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for both tables
CREATE TRIGGER update_formation_duration_after_support_change
    AFTER INSERT OR UPDATE OR DELETE ON supports
    FOR EACH ROW
EXECUTE FUNCTION calculate_formation_duration();

CREATE TRIGGER update_formation_duration_after_quiz_change
    AFTER INSERT OR UPDATE OR DELETE ON quizs
    FOR EACH ROW
EXECUTE FUNCTION calculate_formation_duration();

-- Trigger function for module deletion
CREATE OR REPLACE FUNCTION update_formation_after_module_change()
    RETURNS TRIGGER AS $$
DECLARE
    total_minutes DECIMAL(10,1);
BEGIN
    -- For DELETE operations, recalculate duration for the affected formation
    IF TG_OP = 'DELETE' THEN
        -- Calculate total minutes
        SELECT COALESCE(
                       (
                           SELECT SUM(s.duree)
                           FROM supports s
                                    JOIN modules m ON s.module_id = m.id
                           WHERE m.formation_id = OLD.formation_id
                       ), 0) +
               COALESCE(
                       (
                           SELECT SUM(q.duree)
                           FROM quizs q
                                    JOIN modules m ON q.module_id = m.id
                           WHERE m.formation_id = OLD.formation_id
                       ), 0)
        INTO total_minutes;

        -- Update formation duration (converting minutes to hours)
        UPDATE formations f
        SET duree = ROUND((total_minutes / 60)::numeric, 1)
        WHERE f.id = OLD.formation_id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for module changes
CREATE TRIGGER update_formation_after_module_deletion
    AFTER DELETE ON modules
    FOR EACH ROW
EXECUTE FUNCTION update_formation_after_module_change();
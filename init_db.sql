-- Création des tables
CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- pour UUID

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password TEXT NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL
);

CREATE TABLE administrateurs (
                                 user_id UUID PRIMARY KEY REFERENCES users(id),
                                 departement VARCHAR(100),
                                 access_level INT
);

CREATE TABLE collaborateurs (
                                user_id UUID PRIMARY KEY REFERENCES users(id),
                                poste VARCHAR(100),
                                date_entree DATE
);

CREATE TABLE notifications (
                               id SERIAL PRIMARY KEY,
                               titre TEXT NOT NULL,
                               contenu TEXT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users_notifications (
                                     user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                                     notification_id INT REFERENCES notifications(id) ON DELETE CASCADE,
                                     seen_at TIMESTAMP,
                                     PRIMARY KEY(user_id, notification_id)
);

CREATE TABLE formations (
                            id SERIAL PRIMARY KEY,
                            titre TEXT,
                            description TEXT,
                            type VARCHAR(50),
                            lien_photo TEXT,
                            duree DECIMAL(5,1) DEFAULT 0.0
);

CREATE TABLE collaborateurs_formations (
                                           collaborateur_id UUID REFERENCES collaborateurs(user_id) ON DELETE CASCADE,
                                           formation_id INT REFERENCES formations(id) ON DELETE CASCADE,
                                           progress DECIMAL(5,2),
                                           is_certification_generated BOOLEAN,
                                           PRIMARY KEY (collaborateur_id, formation_id)
);

CREATE TABLE modules (
                         id SERIAL PRIMARY KEY,
                         formation_id INT REFERENCES formations(id) ON DELETE CASCADE,
                         titre TEXT,
                         description TEXT,
                         ordre INTEGER DEFAULT 0
);

-- Add index for performance on order queries
CREATE INDEX idx_modules_formation_ordre ON modules(formation_id, ordre);

CREATE TABLE supports (
                          id SERIAL PRIMARY KEY,
                          module_id INT REFERENCES modules(id) ON DELETE CASCADE,
                          type VARCHAR(50),
                          lien TEXT,
                          titre VARCHAR(255),
                          description TEXT,
                          duree DECIMAL(5,1) DEFAULT 0.0
);

CREATE TABLE quizs (
                       id SERIAL PRIMARY KEY,
                       module_id INT REFERENCES modules(id) ON DELETE CASCADE,
                       titre TEXT,
                       duree DECIMAL(5,1) DEFAULT 0.0,
                       seuil_reussite INTEGER DEFAULT 70
);

CREATE TABLE questions (
                           id SERIAL PRIMARY KEY,
                           quiz_id INT REFERENCES quizs(id) ON DELETE CASCADE,
                           contenu TEXT
);

CREATE TABLE choix (
                       id SERIAL PRIMARY KEY,
                       question_id INT REFERENCES questions(id) ON DELETE CASCADE,
                       contenu TEXT,
                       est_correct BOOLEAN
);


CREATE TABLE logout_tokens (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               token TEXT NOT NULL,
                               logout_date DATE NOT NULL
);

CREATE TABLE collaborateur_support_progress (
                                                collaborateur_id UUID REFERENCES collaborateurs(user_id) ON DELETE CASCADE,
                                                support_id INT REFERENCES supports(id) ON DELETE CASCADE,
                                                seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                PRIMARY KEY (collaborateur_id, support_id)
);

CREATE TABLE collaborateur_module_progress (
                                               collaborateur_id UUID REFERENCES collaborateurs(user_id) ON DELETE CASCADE,
                                               module_id INT REFERENCES modules(id) ON DELETE CASCADE,
                                               is_completed BOOLEAN DEFAULT FALSE,
                                               completed_at TIMESTAMP,
                                               quiz_score DECIMAL(5,2), -- Store the quiz score that completed the module
                                               PRIMARY KEY (collaborateur_id, module_id)
);

CREATE INDEX idx_collaborateur_support_progress_collaborateur ON collaborateur_support_progress(collaborateur_id);
CREATE INDEX idx_collaborateur_support_progress_support ON collaborateur_support_progress(support_id);
CREATE INDEX idx_collaborateur_module_progress_collaborateur ON collaborateur_module_progress(collaborateur_id);
CREATE INDEX idx_collaborateur_module_progress_module ON collaborateur_module_progress(module_id);

INSERT INTO "users" (id, email, password, role,first_name,last_name)
SELECT '223e4567-e89b-12d3-a456-426614174006', 'admin@application.com',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu', 'ADMIN',
       'admin' , 'principal'
WHERE NOT EXISTS (
    SELECT 1
    FROM "users"
    WHERE id = '223e4567-e89b-12d3-a456-426614174006'
       OR email = 'testuser@test.com'
);

INSERT INTO "users" (id, email, password, role,first_name,last_name)
SELECT '223e4567-e89b-12d3-a456-426614174007', 'collaborateur@application.com',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu', 'COLLABORATEUR',
       'collaborateur' , 'principal'
WHERE NOT EXISTS (
    SELECT 1
    FROM "users"
    WHERE id = '223e4567-e89b-12d3-a456-426614174007'
       OR email = 'collaborateur@application.com'
);

----------------------- Notifications -----------------------------



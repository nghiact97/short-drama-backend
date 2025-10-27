-- PostgreSQL Initialization Script
-- Converted from MySQL for Railway deployment

-- ========================
-- 1) Create Tables
-- ========================

-- Short Drama Video Table
CREATE TABLE IF NOT EXISTS video
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(512),
    description VARCHAR(1024),
    video_url   VARCHAR(1024) NOT NULL,
    cover_url   VARCHAR(1024),
    duration_sec INTEGER,
    order_num   INTEGER DEFAULT 0 NOT NULL,
    status      SMALLINT DEFAULT 1 NOT NULL,
    user_id     BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_status_order ON video(status, order_num DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_user_id ON video(user_id);

COMMENT ON TABLE video IS 'Short drama video';
COMMENT ON COLUMN video.id IS 'ID';
COMMENT ON COLUMN video.title IS 'Title';
COMMENT ON COLUMN video.description IS 'Description';
COMMENT ON COLUMN video.video_url IS 'Video URL';
COMMENT ON COLUMN video.cover_url IS 'Cover image URL';
COMMENT ON COLUMN video.duration_sec IS 'Duration in seconds';
COMMENT ON COLUMN video.order_num IS 'Sort order (higher = priority)';
COMMENT ON COLUMN video.status IS 'Status: 0-offline 1-online';
COMMENT ON COLUMN video.user_id IS 'Creator user ID';
COMMENT ON COLUMN video.create_time IS 'Create time';
COMMENT ON COLUMN video.update_time IS 'Update time';
COMMENT ON COLUMN video.is_delete IS 'Is deleted';

-- User Table
CREATE TABLE IF NOT EXISTS user_
(
    id           BIGSERIAL PRIMARY KEY,
    user_account VARCHAR(256) NOT NULL,
    user_password VARCHAR(512) NOT NULL,
    union_id     VARCHAR(256),
    mp_open_id   VARCHAR(256),
    user_name    VARCHAR(256),
    user_avatar  VARCHAR(1024),
    user_profile VARCHAR(512),
    user_role    VARCHAR(256) DEFAULT 'user' NOT NULL,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete    SMALLINT DEFAULT 0 NOT NULL,
    UNIQUE (user_account)
);

CREATE INDEX IF NOT EXISTS idx_union_id ON user_(union_id);
CREATE INDEX IF NOT EXISTS idx_mp_open_id ON user_(mp_open_id);

COMMENT ON TABLE user_ IS 'User';
COMMENT ON COLUMN user_.user_role IS 'User role: user/admin/ban';

-- Drama Table
CREATE TABLE IF NOT EXISTS drama
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(512) NOT NULL,
    description TEXT,
    cover_url   VARCHAR(1024),
    status      SMALLINT DEFAULT 1 NOT NULL,
    view_count  INTEGER DEFAULT 0 NOT NULL,
    category    VARCHAR(128),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_status_view ON drama(status, view_count DESC);
CREATE INDEX IF NOT EXISTS idx_category ON drama(category);

COMMENT ON TABLE drama IS 'Drama series';
COMMENT ON COLUMN drama.status IS 'Status: 0-offline 1-online';

-- Drama-Video Relation (Many-to-Many for episodes)
CREATE TABLE IF NOT EXISTS drama_video
(
    id          BIGSERIAL PRIMARY KEY,
    drama_id    BIGINT NOT NULL,
    video_id    BIGINT NOT NULL,
    episode_num INTEGER NOT NULL DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (drama_id) REFERENCES drama(id) ON DELETE CASCADE,
    FOREIGN KEY (video_id) REFERENCES video(id) ON DELETE CASCADE,
    UNIQUE (drama_id, video_id)
);

CREATE INDEX IF NOT EXISTS idx_drama_id ON drama_video(drama_id);
CREATE INDEX IF NOT EXISTS idx_video_id ON drama_video(video_id);

-- Watch History
CREATE TABLE IF NOT EXISTS watch_history
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    video_id    BIGINT NOT NULL,
    watch_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    watch_position INTEGER DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user_(id) ON DELETE CASCADE,
    FOREIGN KEY (video_id) REFERENCES video(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_watch ON watch_history(user_id, watch_time DESC);
CREATE INDEX IF NOT EXISTS idx_video_watch ON watch_history(video_id);

-- User Drama Favorites
CREATE TABLE IF NOT EXISTS user_drama_favorite
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    drama_id    BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_(id) ON DELETE CASCADE,
    FOREIGN KEY (drama_id) REFERENCES drama(id) ON DELETE CASCADE,
    UNIQUE (user_id, drama_id)
);

CREATE INDEX IF NOT EXISTS idx_user_fav ON user_drama_favorite(user_id);
CREATE INDEX IF NOT EXISTS idx_drama_fav ON user_drama_favorite(drama_id);

-- ========================
-- 2) Insert Sample Data
-- ========================

-- Sample User (password: test123, hashed)
INSERT INTO user_ (user_account, user_password, user_name, user_avatar, user_profile) 
VALUES ('test123', '$2a$10$abcdefghijklmnopqrstuv', 'Test User', 'https://example.com/avatar.jpg', 'Test profile')
ON CONFLICT (user_account) DO NOTHING;

-- Sample Drama
INSERT INTO drama (title, description, cover_url, category, view_count, status)
VALUES 
    ('Sample Drama 1', 'A fascinating story about...', 'https://example.com/cover1.jpg', 'Romance', 1000, 1),
    ('Sample Drama 2', 'An exciting adventure of...', 'https://example.com/cover2.jpg', 'Action', 1500, 1)
ON CONFLICT DO NOTHING;

-- Sample Videos (note: these URLs need to be replaced with actual video URLs)
INSERT INTO video (title, description, video_url, cover_url, duration_sec, order_num, status, user_id)
VALUES 
    ('Episode 1', 'First episode of sample drama', 'https://example.com/video1.mp4', 'https://example.com/cover1.jpg', 300, 1, 1, 1),
    ('Episode 2', 'Second episode of sample drama', 'https://example.com/video2.mp4', 'https://example.com/cover2.jpg', 320, 2, 1, 1)
ON CONFLICT DO NOTHING;

-- Link videos to drama (assumes drama ID 1 and video IDs 1, 2)
INSERT INTO drama_video (drama_id, video_id, episode_num)
VALUES (1, 1, 1), (1, 2, 2)
ON CONFLICT (drama_id, video_id) DO NOTHING;

-- ========================
-- 3) Setup auto-update trigger for update_time
-- ========================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_video_updated_at BEFORE UPDATE ON video FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_updated_at BEFORE UPDATE ON user_ FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_drama_updated_at BEFORE UPDATE ON drama FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


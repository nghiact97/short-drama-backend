-- Simple database initialization
USE short_drama;

-- Create basic tables
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userAccount VARCHAR(256) NOT NULL,
    userPassword VARCHAR(512) NOT NULL,
    userName VARCHAR(256),
    userAvatar VARCHAR(1024),
    userRole VARCHAR(256) DEFAULT 'user',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    isDelete TINYINT DEFAULT 0,
    UNIQUE INDEX uk_userAccount (userAccount)
);

CREATE TABLE IF NOT EXISTS drama (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(512) NOT NULL,
    description VARCHAR(1024),
    coverUrl VARCHAR(1024),
    category VARCHAR(128),
    totalEpisodes INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    orderNum INT DEFAULT 0,
    userId BIGINT NOT NULL,
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    isDelete TINYINT DEFAULT 0,
    INDEX idx_status_order (status, orderNum DESC, id DESC),
    INDEX idx_category (category),
    INDEX idx_userId (userId)
);

CREATE TABLE IF NOT EXISTS video (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(512),
    description VARCHAR(1024),
    videoUrl VARCHAR(1024) NOT NULL,
    coverUrl VARCHAR(1024),
    durationSec INT,
    orderNum INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    userId BIGINT NOT NULL,
    dramaId BIGINT,
    episodeNumber INT,
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    isDelete TINYINT DEFAULT 0,
    INDEX idx_status_order (status, orderNum DESC, id DESC),
    INDEX idx_userId (userId),
    INDEX idx_drama_episode (dramaId, episodeNumber)
);

CREATE TABLE IF NOT EXISTS watch_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    dramaId BIGINT,
    videoId BIGINT NOT NULL,
    episodeNumber INT,
    progress INT DEFAULT 0,
    lastWatchTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_video (userId, videoId),
    INDEX idx_user_time (userId, lastWatchTime DESC),
    INDEX idx_drama (dramaId)
);

CREATE TABLE IF NOT EXISTS user_drama_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    dramaId BIGINT NOT NULL,
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_drama (userId, dramaId),
    INDEX idx_user (userId),
    INDEX idx_drama (dramaId)
);

-- Insert test data
INSERT INTO user (id, userAccount, userPassword, userName, userRole) VALUES
(1, 'admin', '111111', '管理员', 'admin'),
(2, 'creator1', '111111', '内容创作者1', 'user');

INSERT INTO drama (id, title, description, category, totalEpisodes, status, orderNum, userId) VALUES
(1, '都市爱情剧', '一个关于现代都市男女爱情故事的短剧', 'urban', 2, 1, 100, 1),
(2, '古装宫廷剧', '以古代宫廷为背景的权谋爱情剧', 'costume', 1, 1, 90, 1),
(3, '悬疑推理剧', '充满悬疑色彩的推理短剧', 'mystery', 1, 1, 80, 2),
(4, '青春校园剧', '讲述青春校园里的友情、爱情与成长故事', 'youth', 1, 1, 70, 2),
(5, '家庭伦理剧', '以家庭关系为核心的温情短剧', 'family', 1, 1, 60, 1);

INSERT INTO video (title, description, videoUrl, coverUrl, durationSec, orderNum, status, userId, dramaId, episodeNumber) VALUES
('都市爱情剧 第1集', '都市爱情剧第一集', 'https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4', '', 180, 100, 1, 1, 1, 1),
('都市爱情剧 第2集', '都市爱情剧第二集', 'https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4', '', 175, 99, 1, 1, 1, 2),
('古装宫廷剧 第1集', '古装宫廷剧第一集', 'https://drama-1325318408.cos.ap-beijing.myqcloud.com/vid1.mp4', '', 190, 90, 1, 1, 2, 1),
('悬疑推理剧 第1集', '悬疑推理剧第一集', 'https://drama-1325318408.cos.ap-beijing.myqcloud.com/vid1.mp4', '', 200, 80, 1, 2, 3, 1),
('青春校园剧 第1集', '青春校园剧第一集', 'https://drama-1325318408.cos.ap-beijing.myqcloud.com/vid1.mp4', '', 170, 70, 1, 2, 4, 1),
('家庭伦理剧 第1集', '家庭伦理剧第一集', 'https://drama-1325318408.cos.ap-beijing.myqcloud.com/vid1.mp4', '', 165, 60, 1, 1, 5, 1);

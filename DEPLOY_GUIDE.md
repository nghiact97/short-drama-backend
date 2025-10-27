# Hướng dẫn Deploy Server lên Cloud (Production)

## Giải pháp 1: Deploy lên DigitalOcean, AWS, Azure

### Bước 1: Chuẩn bị Server
1. Tạo droplet/server trên:
   - DigitalOcean (giá từ $5/tháng)
   - AWS EC2 (miễn phí tier)
   - Azure
   - Google Cloud

2. Kết nối vào server qua SSH

### Bước 2: Cài đặt MySQL trên Server
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server -y

# Khởi động MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# Tạo database
sudo mysql -e "CREATE DATABASE short_drama CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
sudo mysql -e "CREATE USER 'youruser'@'%' IDENTIFIED BY 'yourpassword';"
sudo mysql -e "GRANT ALL PRIVILEGES ON short_drama.* TO 'youruser'@'%';"
sudo mysql -e "FLUSH PRIVILEGES;"

# Cấu hình cho phép kết nối từ xa
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
# Tìm dòng: bind-address = 127.0.0.1
# Thay đổi thành: bind-address = 0.0.0.0
sudo systemctl restart mysql

# Mở firewall
sudo ufw allow 3306/tcp
```

### Bước 3: Deploy Spring Boot Application
```bash
# Cài đặt Java
sudo apt install openjdk-17-jdk -y

# Cài đặt Maven (nếu chưa có)
sudo apt install maven -y

# Upload code lên server
git clone <your-repo-url>
cd backend

# Build ứng dụng
mvn clean package -DskipTests

# Chạy ứng dụng
nohup java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod > app.log 2>&1 &

# Hoặc tạo systemd service (khuyến nghị)
sudo nano /etc/systemd/system/shortdrama.service
```

Nội dung file systemd:
```ini
[Unit]
Description=Short Drama Backend
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/root/backend
ExecStart=/usr/bin/java -jar target/backend-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# Khởi động service
sudo systemctl daemon-reload
sudo systemctl start shortdrama
sudo systemctl enable shortdrama
sudo systemctl status shortdrama
```

### Bước 4: Mở Port cho Backend
```bash
sudo ufw allow 8101/tcp
```

### Bước 5: Import Database
```bash
# Copy file SQL lên server và import
mysql -u youruser -p short_drama < sql/init_full.sql
```

## Giải pháp 2: Sử dụng Cloud Database (Khuyến nghị)

### AWS RDS
1. Tạo MySQL instance trên AWS RDS
2. Kết nối endpoint từ ứng dụng Spring Boot

### ClearDB (Free MySQL Hosting)
1. Truy cập: https://www.cleardb.com/
2. Tạo database miễn phí
3. Lấy connection string và cập nhật vào application.yml

## Cập nhật Configuration

File `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: "jdbc:mysql://YOUR_SERVER_IP:3306/short_drama?useSSL=false&allowPublicKeyRetrieval=true"
    username: "youruser"
    password: "yourpassword"
```

## Lưu ý bảo mật:
1. Sử dụng strong password cho MySQL
2. Giới hạn IP truy cập MySQL (nếu có thể)
3. Sử dụng SSL cho MySQL connection
4. Cấu hình firewall chặt chẽ
5. Thường xuyên update server


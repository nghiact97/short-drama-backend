# Hướng dẫn nhanh: Expose Server để truy cập từ điện thoại

## 🚀 Phương án nhanh nhất: Ngrok (5 phút)

### Bước 1: Cài đặt Ngrok
- Tải từ: https://ngrok.com/download
- Giải nén vào thư mục bất kỳ

### Bước 2: Đăng ký và lấy token
- Đăng ký: https://dashboard.ngrok.com/signup
- Lấy authtoken: https://dashboard.ngrok.com/get-started/your-authtoken

### Bước 3: Chạy lệnh
```bash
# Thêm authtoken
ngrok config add-authtoken YOUR_AUTH_TOKEN

# Mở 2 terminal/command prompt riêng:

# Terminal 1: Khởi động Spring Boot
cd F:\Flutter\short-drama\backend
mvn spring-boot:run

# Terminal 2: Khởi động ngrok
ngrok http 8101
```

### Bước 4: Lấy URL công khai
Bạn sẽ thấy URL như:
```
Forwarding: https://xxxx-xxxx.ngrok-free.app
```

### Bước 5: Cập nhật Flutter app
Thay đổi base URL trong Flutter app từ `localhost:8101` thành URL ngrok

---

## 🗄️ Kết nối MySQL từ xa

### Option 1: Sử dụng Free MySQL Cloud (Khuyến nghị)

**ClearDB:**
1. Đăng ký: https://www.cleardb.com/
2. Tạo database instance miễn phí
3. Lấy connection string
4. Cập nhật vào `application.yml`

**PlanetScale:**
1. Đăng ký: https://planetscale.com/
2. Tạo database
3. Lấy MySQL connection string
4. Cập nhật vào `application.yml`

### Option 2: Expose MySQL local qua ngrok
```bash
# Khởi động ngrok cho MySQL
ngrok tcp 3306

# Bạn sẽ nhận được: tcp://0.tcp.ngrok.io:XXXXX
# Cập nhật application.yml với URL này
```

### Option 3: Cấu hình MySQL cho phép kết nối từ xa

Nếu bạn đang chạy MySQL trên máy khác trong cùng mạng LAN:

1. Mở MySQL config:
```bash
# Windows
# Tìm file my.ini trong thư mục MySQL
# Thường ở: C:\ProgramData\MySQL\MySQL Server 8.0\my.ini

# Linux
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
```

2. Tìm và sửa:
```ini
bind-address = 0.0.0.0
```

3. Restart MySQL:
```bash
# Windows - Services
# Tìm MySQL service và restart

# Linux
sudo systemctl restart mysql
```

4. Cho phép user remote access:
```sql
mysql -u root -p
CREATE USER 'remote_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON short_drama.* TO 'remote_user'@'%';
FLUSH PRIVILEGES;
```

---

## 📱 Test từ điện thoại

1. Đảm bảo điện thoại và máy tính cùng WiFi (hoặc dùng ngrok)
2. Lấy IP của máy tính:
   ```bash
   ipconfig  # Windows
   ifconfig  # Linux/Mac
   ```
3. Test kết nối:
   ```
   http://YOUR_LOCAL_IP:8101/api/health
   ```
   hoặc nếu dùng ngrok:
   ```
   https://xxxx.ngrok-free.app/api/health
   ```

---

## ⚠️ Lưu ý quan trọng

1. **Ngrok free**: URL sẽ thay đổi mỗi lần khởi động
2. **Bảo mật**: Ngrok free có watermark ngăn chặn abuse
3. **Production**: Nên deploy lên cloud server thật sự
4. **Database**: Không nên expose MySQL local ra Internet trực tiếp

---

## 🔧 Cấu hình cho Production

Xem file `DEPLOY_GUIDE.md` để biết cách deploy lên cloud server.


# 🚀 Deploy lên Render.com - HOÀN TOÀN MIỄN PHÍ

## Bước 1: Chuẩn bị GitHub

1. **Push code lên GitHub** (nếu chưa có):
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/short-drama-backend.git
   git push -u origin main
   ```

## Bước 2: Tạo tài khoản Render

1. Truy cập: https://render.com
2. Đăng nhập bằng GitHub
3. Chọn **"New"** → **"Blueprint"**

## Bước 3: Deploy Backend

### Cách 1: Sử dụng Blueprint (Tự động - Khuyến nghị)

1. Trong GitHub repo, **render.yaml** đã sẵn sàng
2. Trên Render dashboard:
   - Chọn **"New"** → **"Blueprint"**
   - Connect repository
   - Chọn repo của bạn
   - Click **"Apply"**
   - Render sẽ tự động tạo cả Web Service và Database!

### Cách 2: Manual Setup

#### 3.1. Tạo Database trước

1. Dashboard → **"New"** → **"PostgreSQL"**
2. Settings:
   - Name: `shortdramadb`
   - Plan: **Free**
   - PostgreSQL Version: Latest
3. Click **"Create Database"**
4. Lưu lại:
   - **Internal Database URL**
   - **External Connection String**

#### 3.2. Tạo Web Service

1. Dashboard → **"New"** → **"Web Service"**
2. Connect GitHub repository
3. Settings:
   - **Name**: `short-drama-backend`
   - **Region**: Singapore (gần Việt Nam nhất)
   - **Branch**: main
   - **Root Directory**: (để trống hoặc `backend`)
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres`
   - **Plan**: Free

4. **Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE = postgres
   SPRING_DATASOURCE_URL = (lấy từ Database Internal URL)
   DB_USER = (user từ database)
   DB_PASSWORD = (password từ database)
   PORT = 10000
   ```

5. Click **"Create Web Service"**

## Bước 4: Import Database

### Cách 1: Qua Render Dashboard
1. Vào database service
2. Click **"Connect"** → **"psql"**
3. Copy connection string và connect từ terminal

### Cách 2: Qua Command line
```bash
# Cài đặt PostgreSQL client
# Download từ: https://www.postgresql.org/download/

# Connect
psql "postgresql://user:password@host/dbname"

# Import
\i sql/init_full.sql
```

### Cách 3: Convert MySQL SQL sang PostgreSQL
Nếu database của bạn là MySQL:
- Dùng tool: https://github.com/lanyjinyu/sql-convert
- Hoặc dùng: https://dbconvert.com/mysql/postgresql/

## Bước 5: Lấy URL công khai

Sau khi deploy xong, bạn sẽ có URL dạng:
```
https://short-drama-backend.onrender.com
```

URL này có thể truy cập từ mọi thiết bị!

---

## ⚠️ Lưu ý quan trọng

### Free Tier Restrictions:
1. **Sleeping**: Service sẽ sleep sau 15 phút không có traffic
   - Giải pháp: Setup cron job gọi service mỗi 5 phút (free tools)
   
2. **Database**: 90 ngày free trial, sau đó $7/tháng
   - Giải pháp: Dùng Supabase hoặc Neon (miễn phí mãi mãi)

3. **Build time**: Mỗi build sẽ tốn ~3-5 phút

### Cập nhật sau khi deploy:
Flutter app của bạn cần đổi base URL thành:
```
https://short-drama-backend.onrender.com/api
```

---

## 🎯 Bước tiếp theo

Sau khi deploy thành công:
1. Test API: `https://YOUR_URL.onrender.com/api/doc.html`
2. Update Flutter app với URL mới
3. Test từ điện thoại

---

## 📱 Test từ điện thoại

1. Kết nối WiFi hoặc dữ liệu di động
2. Mở Flutter app
3. App sẽ tự động kết nối đến server trên cloud!

**Không cần ngrok, không cần port forwarding, không cần config gì thêm!**


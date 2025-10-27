# 🚀 BẮT ĐẦU NGAY - HOÀN TOÀN MIỄN PHÍ

## CÁCH NHANH NHẤT: Railway.app (5 phút)

### Bước 1: Push lên GitHub
```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git push -u origin main
```

### Bước 2: Deploy trên Railway
1. Truy cập: https://railway.app
2. Login bằng GitHub
3. **"New Project"** → **"Deploy from GitHub repo"**
4. Chọn repository của bạn
5. Đợi ~3 phút để build
6. Railway tự động tạo database!
7. Copy URL: `https://xxx.up.railway.app`

### Bước 3: Import Database
1. Trong Railway project → **"+ New"** → **"Database"** → **"PostgreSQL"**
2. Chờ database created
3. Vào database service → **"Connect"** → Copy connection string
4. Chạy terminal:
```bash
# Install psql (PostgreSQL client)
# Windows: https://www.postgresql.org/download/
psql "your-connection-string-from-railway" < sql/init_full.sql
```

### Bước 4: Update Flutter App
Thay đổi base URL trong Flutter app:
```
DỒNG KIA: http://localhost:8101/api
THÀNH: https://xxx.up.railway.app/api
```

### Bước 5: Chạy Flutter App
App của bạn giờ có thể kết nối từ mọi nơi! 📱

---

## 🔄 Nếu Railway không hoạt động → Dùng Render.com

### Render.com (miễn phí mãi mãi)

1. **Đăng ký**: https://render.com (login bằng GitHub)
2. **"New"** → **"Web Service"** → Connect GitHub repo
3. Settings:
   ```
   Build: mvn clean package -DskipTests
   Start: java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres
   ```
4. **"New"** → **"PostgreSQL"** (Free plan)
5. Add environment variables từ database
6. Deploy!

**Lưu ý**: Render có free tier nhưng sẽ sleep sau 15 phút không dùng.
Giải pháp: Setup Uptime Robot (miễn phí) để keep alive.

---

## 🐳 Hoặc dùng Docker Local + Ngrok

### Chạy local với Docker:
```bash
# Build và start
docker-compose up -d

# Start ngrok
ngrok http 8101
```

Copy URL ngrok và dùng trong Flutter app.

---

## 📝 Summary: Chọn cách nào?

| Mục đích | Khuyến nghị |
|----------|-------------|
| Production | Railway.app (không sleep, ổn định) |
| Test nhanh | Docker + Ngrok |
| Free forever | Render.com |
| Database riêng | Supabase hoặc Neon |

---

## ✅ Checklist

- [ ] Push code lên GitHub
- [ ] Tạo account Railway (hoặc Render)
- [ ] Deploy backend
- [ ] Tạo database
- [ ] Import data
- [ ] Test URL hoạt động
- [ ] Update Flutter app
- [ ] Test từ điện thoại

---

## 🎯 BẠN NÊN LÀM GÌ BÂY GIỜ?

**Option 1 (Khuyến nghị nhất)**: Railway.app
- Mở: https://railway.app
- Follow: `DEPLOY_RAILWAY_GUIDE.md`

**Option 2**: Render.com
- Mở: https://render.com  
- Follow: `DEPLOY_RENDER_GUIDE.md`

**Option 3**: Local Docker + Ngrok
- Chạy: `docker-compose up -d`
- Chạy: `ngrok http 8101`

**Chọn 1 trong 3 và làm ngay! Mất ~5 phút thôi! 🚀**


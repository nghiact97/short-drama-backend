# 📋 Checklist Sau Khi Deploy Lên Railway

## ✅ Bước 1: Kiểm tra Deployment

### 1.1. Xem logs trên Railway
1. Vào [Railway Dashboard](https://railway.app)
2. Chọn project **short-drama-backend**
3. Click vào service → **View Logs**
4. Kiểm tra log có thông báo:
   ```
   Started MainApplication in X seconds
   ```

### 1.2. Lấy URL Public
1. Trong Railway Dashboard → Settings → **Generate Domain**
2. URL có dạng: `https://short-drama-backend-production.up.railway.app`
3. Hoặc dùng custom domain nếu đã setup

---

## ✅ Bước 2: Test API Endpoints

### 2.1. Test Health Check
```bash
# Thay YOUR_URL bằng URL từ Railway
curl https://YOUR_URL/actuator/health

# Hoặc dùng browser mở:
https://YOUR_URL/actuator/health
```

### 2.2. Test API Endpoints Chính

#### **User APIs**
```bash
# 1. Đăng ký
POST https://YOUR_URL/api/user/register
{
  "userAccount": "test123",
  "userPassword": "password123",
  "checkPassword": "password123"
}

# 2. Đăng nhập
POST https://YOUR_URL/api/user/login
{
  "userAccount": "test123",
  "userPassword": "password123"
}

# 3. Lấy thông tin user hiện tại
GET https://YOUR_URL/api/user/get/login
Cookie: [session từ login]
```

#### **Drama APIs**
```bash
# 1. Lấy danh sách drama
GET https://YOUR_URL/api/drama/list?current=1&pageSize=10

# 2. Tìm kiếm drama
GET https://YOUR_URL/api/drama/search?searchText=test&current=1&pageSize=10

# 3. Lấy chi tiết drama
GET https://YOUR_URL/api/drama/1

# 4. Lấy danh sách episodes
GET https://YOUR_URL/api/drama/1/episodes
```

#### **Video APIs**
```bash
# 1. Lấy video feed
GET https://YOUR_URL/api/video/feed?current=1&pageSize=10

# 2. Lấy thông tin video
GET https://YOUR_URL/api/video/get?id=1
```

#### **AI APIs**
```bash
# Test AI endpoint
POST https://YOUR_URL/api/ai/ask
{
  "question": "Xin chào"
}
```

---

## ✅ Bước 3: Setup Database (Quan trọng!)

### Nếu chưa có data trong PostgreSQL:
1. Vào Railway Dashboard → Click vào **PostgreSQL** service
2. Copy **Connection String**
3. Kết nối vào database và chạy script init:
   - File: `sql/init_full.sql` hoặc `sql/basic_init.sql`

### Cách kết nối PostgreSQL từ Railway:
```bash
# Lấy connection string từ Railway
DATABASE_URL=postgresql://user:password@host:port/dbname

# Sử dụng psql hoặc GUI tool (pgAdmin, DBeaver)
```

---

## ✅ Bước 4: Test Từ Flutter App

### 4.1. Update URL trong Flutter App
```dart
// Thay đổi trong config file của Flutter app
final String baseUrl = "https://YOUR_URL/api";

// Ví dụ: lib/config/api_config.dart
const String BASE_URL = "https://short-drama-backend-production.up.railway.app/api";
```

### 4.2. Test các chức năng chính
- [ ] Đăng ký/Đăng nhập
- [ ] Xem danh sách drama
- [ ] Xem chi tiết drama
- [ ] Xem video
- [ ] Tìm kiếm drama
- [ ] Lịch sử xem
- [ ] Yêu thích drama

---

## ✅ Bước 5: Monitor & Debug

### 5.1. Monitor Logs
```bash
# Railway tự động cung cấp logs
# Vào Railway Dashboard → Deployments → Logs
```

### 5.2. Các lỗi thường gặp
- ❌ **Database không kết nối được**: Kiểm tra `DATABASE_URL` env var
- ❌ **404 Not Found**: Kiểm tra context path `/api`
- ❌ **500 Internal Server Error**: Xem logs trong Railway
- ❌ **Build failed**: Kiểm tra Maven build command

### 5.3. Environment Variables cần thiết
```bash
# Railway tự động set các biến này:
- DATABASE_URL (từ PostgreSQL service)
- PORT (Railway tự set)
- NODE_ENV (hoặc production)

# Có thể thêm custom env vars:
- SPRING_PROFILES_ACTIVE=prod
- SERVER_PORT=10000
```

---

## 🔧 Tools để Test API

### Option 1: Postman
1. Import cURL commands vào Postman
2. Tạo collection cho các endpoints
3. Test từng endpoint

### Option 2: cURL
```bash
# Test với cURL
curl -X GET "https://YOUR_URL/api/drama/list?current=1&pageSize=10" \
  -H "Content-Type: application/json"
```

### Option 3: Thunder Client (VS Code)
- Cài extension Thunder Client
- Import requests
- Test trực tiếp trong VS Code

---

## 📊 Next Steps

### Production Ready Checklist:
- [ ] Database đã có data
- [ ] API response time < 500ms
- [ ] CORS config đúng
- [ ] Security headers setup
- [ ] Rate limiting (nếu cần)
- [ ] Monitoring & alerting
- [ ] Backup database định kỳ

### Optional Enhancements:
- [ ] Setup custom domain
- [ ] SSL certificate (Railway tự động)
- [ ] CDN cho static files
- [ ] Redis cache (cho production)
- [ ] Email service (notifications)

---

## 🆘 Troubleshooting

### Nếu app không start:
```bash
# Check logs
railway logs

# Check environment
railway variables

# Re-deploy
git push (auto deploy từ GitHub)
```

### Nếu database error:
```bash
# Kiểm tra DATABASE_URL
echo $DATABASE_URL

# Test connection
psql $DATABASE_URL
```

---

## 📞 Support
- [Railway Docs](https://docs.railway.app)
- [Railway Discord](https://discord.gg/railway)


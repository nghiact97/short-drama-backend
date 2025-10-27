# 🌐 Hướng Dẫn Lấy API URL Từ Railway

## 📍 Bước 1: Truy cập Railway Dashboard

1. Đăng nhập vào [Railway Dashboard](https://railway.app)
2. Chọn project **short-drama-backend**

---

## 📍 Bước 2: Lấy Public URL

### Cách 1: Từ Service Settings (Khuyến nghị)

1. Click vào **Service** của bạn (trong danh sách services)
2. Click tab **Settings** (⚙️)
3. Cuộn xuống phần **Networking**
4. Bạn sẽ thấy:
   ```
   Public Domain: https://short-drama-backend-production.up.railway.app
   ```
5. Click **Generate Domain** nếu chưa có
6. **Copy URL này** → Đây là base URL của bạn!

### Cách 2: Từ Service Overview

1. Vào **Service Overview**
2. Tìm phần **Networking** hoặc **Environment**
3. Copy **Public URL**

---

## 📍 Bước 3: Tạo URL API hoàn chỉnh

Railway URL có dạng:
```
https://SHORT-DRAMA-BACKEND-PRODUCTION.railway.app
```

**API Base URL** sẽ là:
```
https://SHORT-DRAMA-BACKEND-PRODUCTION.railway.app/api
```

> ⚠️ **Lưu ý**: Thêm `/api` vào cuối vì trong code có `context-path: /api`

---

## 📍 Bước 4: Test API URL

### Test Health Check
```bash
curl https://YOUR_RAILWAY_URL/api/drama/list?current=1&pageSize=10
```

### Test trong Browser
Mở trình duyệt và truy cập:
```
https://YOUR_RAILWAY_URL/api/drama/list?current=1&pageSize=10
```

### Nếu muốn Health Check
```bash
curl https://YOUR_RAILWAY_URL/api/actuator/health
```

---

## 📍 Bước 5: Cập nhật Flutter App

### Option 1: Trong Code

Thêm vào file config của Flutter app (ví dụ: `lib/config/api_config.dart`):

```dart
class ApiConfig {
  // Production URL từ Railway
  static const String baseUrl = "https://YOUR_RAILWAY_URL/api";
  
  // Local development URL
  // static const String baseUrl = "http://localhost:8101/api";
  
  // Ngrok URL (local testing)
  // static const String baseUrl = "https://xxxx.ngrok.io/api";
}
```

### Option 2: Environment Variables

Sử dụng environment variables:

```dart
class ApiConfig {
  static String get baseUrl {
    // Check environment
    const env = String.fromEnvironment('ENV', defaultValue: 'production');
    
    switch (env) {
      case 'local':
        return 'http://localhost:8101/api';
      case 'ngrok':
        return 'https://YOUR_NGROK_URL/api';
      default:
        return 'https://YOUR_RAILWAY_URL/api';
    }
  }
}
```

---

## 📍 Bước 6: Custom Domain (Optional)

Nếu muốn custom domain (ví dụ: `api.shortdrama.com`):

1. Trong Railway Settings → **Networking**
2. Click **Add Custom Domain**
3. Nhập domain: `api.yourdomain.com`
4. Setup DNS records theo hướng dẫn
5. Railway sẽ tự động tạo SSL certificate

---

## 📍 Bước 7: Kiểm tra Deployment

### Kiểm tra Logs
1. Vào **Deployments** tab
2. Click vào deployment mới nhất
3. Xem logs để đảm bảo app đã start thành công

Log sẽ hiển thị:
```
Started MainApplication in X.XXX seconds
Tomcat started on port(s): 8101 (http)
```

### Kiểm tra Build
Kiểm tra build đã thành công:
```
✓ Deploy Succeeded
Building...
✓ Built successfully
```

---

## 🔧 Troubleshooting

### ❌ 404 Not Found
- Kiểm tra context path `/api`
- Thử truy cập endpoint đơn giản trước: `/api/drama/list`

### ❌ 500 Internal Server Error
- Xem logs trong Railway
- Kiểm tra database connection
- Kiểm tra environment variables

### ❌ Connection Refused
- Service chưa deploy xong
- Đợi deployment hoàn thành (sẽ mất 2-3 phút)

### ❌ CORS Error
- Railway tự động xử lý CORS
- Kiểm tra CORS config trong code

---

## 📱 Complete Example

### 1. Railway URL
```
https://short-drama-backend-production.up.railway.app
```

### 2. API Endpoints
```bash
# Base URL
https://short-drama-backend-production.up.railway.app/api

# Danh sách drama
GET https://short-drama-backend-production.up.railway.app/api/drama/list

# Chi tiết drama
GET https://short-drama-backend-production.up.railway.app/api/drama/1

# Danh sách episodes
GET https://short-drama-backend-production.up.railway.app/api/drama/1/episodes

# Tìm kiếm
GET https://short-drama-backend-production.up.railway.app/api/drama/search?searchText=test

# Video feed
GET https://short-drama-backend-production.up.railway.app/api/video/feed

# User login
POST https://short-drama-backend-production.up.railway.app/api/user/login

# AI
POST https://short-drama-backend-production.up.railway.app/api/ai/ask
```

---

## 📝 Notes

- ✅ Railway tự động tạo public URL
- ✅ SSL certificate tự động (HTTPS)
- ✅ Không cần cấu hình thêm
- ✅ URL sẽ không thay đổi sau khi generate
- ⚠️ Thêm `/api` vào cuối URL vì `context-path: /api`
- ⚠️ URL chỉ active khi service đang chạy

---

## 🎯 Quick Reference

```bash
# Lấy URL
1. Railway Dashboard → Service → Settings → Networking
2. Copy Public Domain
3. Thêm /api vào cuối

# Test
curl https://YOUR_URL/api/drama/list?current=1&pageSize=10

# Update Flutter
const String BASE_URL = "https://YOUR_URL/api";
```


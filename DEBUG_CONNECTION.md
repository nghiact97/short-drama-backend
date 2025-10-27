# 🐛 Debug Connection Issues

## URL của bạn:
```
outstanding-gentleness-production-0c78.up.railway.app
```

---

## ✅ Bước 1: Kiểm tra Service đã start chưa?

### Trong Railway Dashboard:
1. Vào **Deployments** tab
2. Click vào deployment mới nhất
3. **Xem Logs** - tìm dòng:
   ```
   Started MainApplication in X.XXX seconds
   Tomcat started on port(s): XXXX
   ```

❌ **Nếu thấy error** → Ghi lại error message

❌ **Nếu build failed** → Kiểm tra build logs

---

## ✅ Bước 2: Test URL

### Test với browser:
```
https://outstanding-gentleness-production-0c78.up.railway.app/api/drama/list
```

### Test với curl:
```bash
curl https://outstanding-gentleness-production-0c78.up.railway.app/api/drama/list
```

### Test Health Check:
```bash
curl https://outstanding-gentleness-production-0c78.up.railway.app/actuator/health
```

---

## ⚠️ Các lỗi thường gặp:

### 1️⃣ 404 Not Found
**Nguyên nhân**: Service chưa start hoặc URL sai

**Fix**:
- Đảm bảo thêm `/api` vào cuối URL
- Kiểm tra logs trong Railway

### 2️⃣ 502 Bad Gateway
**Nguyên nhân**: Service đã stop hoặc crash

**Fix**:
- Restart service trong Railway
- Kiểm tra logs để tìm lỗi

### 3️⃣ 503 Service Unavailable
**Nguyên nhân**: Database chưa kết nối được

**Fix**:
- Kiểm tra PostgreSQL service đã active chưa
- Kiểm tra DATABASE_URL environment variable

### 4️⃣ Connection Timed Out
**Nguyên nhân**: Service đang build hoặc sleep

**Fix**:
- Đợi deployment hoàn thành (2-3 phút)
- Tắt "sleepApplication" trong railway.json

### 5️⃣ Database Connection Error
**Nguyên nhân**: Database chưa setup

**Fix**:
```bash
# Kiểm tra DATABASE_URL
Railway → PostgreSQL Service → Variables
DATABASE_URL=postgresql://...

# Kiểm tra app kết nối được không
# Xem logs sẽ có error database
```

---

## 🔍 Các nguyên nhân cụ thể:

### A. PORT Configuration
Railway tự động set biến `PORT`, code cần đọc biến này:

**application-prod.yml**:
```yaml
server:
  port: ${PORT:8101}
```

### B. Context Path
URL phải có `/api` ở cuối

**ĐÚNG**:
```
https://outstanding-gentleness-production-0c78.up.railway.app/api/drama/list
```

**SAI**:
```
https://outstanding-gentleness-production-0c78.up.railway.app/drama/list
```

### C. Database Connection
Nếu app crash vì database:

1. **Kiểm tra PostgreSQL service**:
   - Railway Dashboard → PostgreSQL Service → Active?

2. **Kiểm tra connection**:
   ```bash
   # Xem trong Railway logs
   "Failed to obtain JDBC Connection"
   ```

3. **Fix**:
   - Thêm PostgreSQL service
   - Copy DATABASE_URL
   - App sẽ tự động connect

### D. Build Failed
Nếu build fail:

1. **Check Dockerfile**:
   ```dockerfile
   FROM openjdk:8-jdk-slim
   WORKDIR /app
   COPY . .
   RUN ./mvnw clean package -DskipTests
   CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
   ```

2. **Check Maven version**: Railway nixpacks cần Maven 3.8+

3. **Check build logs** trong Railway

---

## 🎯 Quick Fixes

### Fix 1: Restart Service
```
Railway → Service → Settings → Restart
```

### Fix 2: Rebuild
```
Railway → Settings → Clear Build Cache
→ Deploy again
```

### Fix 3: Check Environment
```
Railway → Variables
- PORT (auto-set)
- DATABASE_URL (auto-set nếu có PostgreSQL)
- SPRING_PROFILES_ACTIVE=prod
```

### Fix 4: Check Logs
```
Railway → Deployments → Latest → View Logs
```

Tìm các dòng:
- ✅ "Started MainApplication"
- ❌ "Error"
- ❌ "Exception"
- ❌ "Failed"

---

## 📝 Action Plan

### 1. Lấy Logs từ Railway
Copy toàn bộ logs từ deployment mới nhất

### 2. Test URL
```bash
# Test 1: Base URL
curl https://outstanding-gentleness-production-0c78.up.railway.app

# Test 2: API endpoint
curl https://outstanding-gentleness-production-0c78.up.railway.app/api/drama/list

# Test 3: Health check
curl https://outstanding-gentleness-production-0c78.up.railway.app/actuator/health
```

### 3. Check URL Construction
```dart
// Flutter app
final String baseUrl = "https://outstanding-gentleness-production-0c78.up.railway.app/api";
```

---

## 🆘 Still Not Working?

**Cho tôi biết**:
1. Error message trong logs (copy full log)
2. Response khi test URL (status code)
3. Screenshot Railway logs

**Tôi sẽ giúp debug!**


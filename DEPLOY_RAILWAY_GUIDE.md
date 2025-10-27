# 🚂 Deploy lên Railway.app - HOÀN TOÀN MIỄN PHÍ

## Bước 1: Đăng ký Railway

1. Truy cập: https://railway.app
2. Click **"Start a New Project"**
3. Đăng nhập bằng **GitHub**
4. Authorize Railway

## Bước 2: Deploy từ GitHub

### Tự động nhất:

1. **New Project** → **"Deploy from GitHub repo"**
2. Chọn repository của bạn
3. Railway sẽ tự động:
   - Detect Java project
   - Build với Maven
   - Deploy app
   - **Tự động tạo và kết nối database!**

### Manual nếu cần:

1. **New Project** → **"Empty Project"**
2. Click **"+ New"** → **"GitHub Repo"**
3. Chọn repo
4. Railway sẽ tự động deploy

## Bước 3: Thêm Database PostgreSQL

1. Trong project dashboard, click **"+ New"**
2. Chọn **"Database"** → **"Add PostgreSQL"**
3. Railway tự động:
   - Tạo database
   - Tạo environment variables
   - Connect với app

## Bước 4: Environment Variables

Railway tự động tạo các biến môi trường:
```
DATABASE_URL = postgresql://...
DB_USER = ...
DB_PASSWORD = ...
```

Để kiểm tra:
1. Vào **"Variables"** tab
2. Xem các variables đã tự động tạo

## Bước 5: Settings

1. **Settings** tab
2. **Build Command**: (để mặc định hoặc `mvn clean package -DskipTests`)
3. **Start Command**: 
   ```
   java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres
   ```
4. **Healthcheck Path**: `/api/health` (nếu có)

## Bước 6: Deploy

1. Railway tự động deploy khi push lên GitHub
2. Hoặc click **"Redeploy"** trong dashboard
3. Chờ vài phút để build

## Bước 7: Lấy URL

Sau khi deploy xong, bạn sẽ thấy:
```
Your app is live at: https://YOUR_PROJECT_NAME.up.railway.app
```

Click URL để mở hoặc copy để dùng trong Flutter app!

---

## 🔄 Auto-Deploy

Railway tự động deploy khi bạn push code lên GitHub branch `main`.

Mỗi lần push:
1. GitHub webhook gọi Railway
2. Railway build lại
3. Railway deploy version mới
4. Service tự động restart

---

## 💾 Database Access

### Truy cập qua Railway Dashboard:
1. Vào database service
2. Click **"Data"** tab
3. Click **"Query"** để chạy SQL

### Import từ file:
1. Vào database service
2. Click **"Connect"**
3. Copy connection string
4. Run từ terminal:
```bash
psql "postgresql://..." < sql/init_full.sql
```

---

## 🎁 Free Tier Benefits

Railway cho bạn:
- ✅ **$5 credit free/tháng**
- ✅ No sleeping (không bao giờ ngủ)
- ✅ Unlimited bandwidth
- ✅ SSL tự động
- ✅ Custom domain (free!)
- ✅ Auto-deploy từ GitHub

---

## 📊 So sánh với Render

| Feature | Railway | Render |
|---------|---------|--------|
| Free Credit | $5/tháng | Free forever |
| Sleeping | ❌ Không sleep | ✅ Sleep sau 15 phút |
| Database Free | ✅ PostgreSQL free | Trial 90 ngày |
| Auto-deploy | ✅ | ✅ |
| Setup Time | 2 phút | 5 phút |

---

## 🚀 Test ngay!

Sau khi deploy xong Railway:
1. Copy URL: `https://xxx.up.railway.app`
2. Update Flutter app
3. Test ngay từ điện thoại!

**Railway nhanh hơn và ổn định hơn cho free tier!**


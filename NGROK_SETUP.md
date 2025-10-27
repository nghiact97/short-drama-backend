# Hướng dẫn Expose Server với Ngrok (Phát triển)

## Bước 1: Cài đặt Ngrok

### Windows:
1. Tải ngrok từ: https://ngrok.com/download
2. Giải nén và đặt vào một thư mục (ví dụ: C:\ngrok)
3. Thêm ngrok vào PATH hoặc chạy trực tiếp từ thư mục

## Bước 2: Tạo tài khoản và lấy Auth Token
1. Đăng ký tại: https://dashboard.ngrok.com/signup
2. Lấy authtoken từ: https://dashboard.ngrok.com/get-started/your-authtoken
3. Chạy lệnh:
```bash
ngrok config add-authtoken YOUR_AUTH_TOKEN
```

## Bước 3: Khởi động ngrok tunnel
```bash
ngrok http 8101
```

## Bước 4: Lấy URL công khai
Sau khi chạy ngrok, bạn sẽ nhận được URL như:
```
Forwarding: https://xxxx-xxxx-xxxx.ngrok-free.app
```

## Bước 5: Kết nối MySQL từ xa

### Option 1: Sử dụng MySQL trên Cloud (Khuyến nghị)
Thay vì MySQL local, sử dụng:
- AWS RDS
- Azure MySQL
- Google Cloud SQL
- Free Hosting: https://www.freemysqlhosting.net/

### Option 2: Sử dụng ngrok cho MySQL (Không khuyến nghị cho production)
```bash
ngrok tcp 3306
```
Sau đó cập nhật URL database trong application.yml

## Cập nhật configuration

Thay đổi `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: "jdbc:mysql://YOUR_MYSQL_HOST:3306/short_drama"
```

## Lưu ý:
- URL ngrok miễn phí sẽ thay đổi mỗi lần khởi động (trừ khi dùng plan trả phí)
- Ngrok free có giới hạn bandwidth
- Không nên dùng cho production


# ELK trên production — checklist deploy

Tab **Logs** trong Admin Portal đọc log qua backend proxy `GET /admin/logs`
(chỉ ROLE_ADMIN). Elasticsearch/Kibana **không cần** và **không nên** mở ra
internet.

## 1. Deploy backend mới

Bản backend có `AdminLogController` + `ElasticsearchLogService`. Trên server:

```bash
git pull
docker compose up -d --build backend
```

`docker-compose.yml` đã cấu hình sẵn cho container backend:

| Biến | Giá trị | Ý nghĩa |
|---|---|---|
| `LOGSTASH_HOST` | `logstash` | Backend đẩy log vào Logstash qua TCP 5044 |
| `ELASTICSEARCH_URL` | `http://elasticsearch:9200` | Backend đọc log cho Admin Portal |

Không cần sửa `.env` trên server — hai biến này được set thẳng trong compose
(tên service trong cùng docker network).

## 2. Kiểm tra sau deploy

```bash
# Endpoint tồn tại (401 = đúng, vì chưa gửi JWT):
curl -s -o /dev/null -w "%{http_code}\n" https://api.labedu.online/api/v1/admin/logs

# Log đã chảy vào ES chưa:
docker compose exec elasticsearch curl -s "localhost:9200/_cat/indices/ar-chemistry-be-logs-*?v"
```

Nếu index trống: gọi vài API bất kỳ để backend sinh log, chờ vài giây rồi
kiểm tra lại.

## 3. Kibana (tùy chọn — chỉ cho nút "Mở Kibana")

Kibana đang chạy **không có xác thực** (`xpack.security.enabled=false`).
TUYỆT ĐỐI không map port 5601 ra ngoài. Hai cách truy cập an toàn:

**Cách A — SSH tunnel (khuyên dùng, không cần cấu hình gì):**

```bash
ssh -L 5601:localhost:5601 user@<server-ip>
# rồi mở http://localhost:5601 trên máy mình
```

**Cách B — reverse proxy nginx + mật khẩu (khi cần truy cập thường xuyên):**

```nginx
server {
    server_name kibana.labedu.online;
    listen 443 ssl;
    # ... ssl_certificate ...

    location / {
        auth_basic           "Kibana";
        auth_basic_user_file /etc/nginx/.htpasswd;   # tạo bằng: htpasswd -c
        proxy_pass           http://localhost:5601;
        proxy_set_header     Host $host;
        proxy_set_header     Upgrade $http_upgrade;
        proxy_set_header     Connection "upgrade";
    }
}
```

Sau đó đặt `KIBANA_URL=https://kibana.labedu.online` trong `.env` của app
khi build bản production.

## 4. Lưu ý dung lượng

Index log tạo theo ngày (`ar-chemistry-be-logs-YYYY.MM.dd`) và không tự xoá.
Dọn định kỳ (giữ 14 ngày) bằng cron trên server:

```bash
# xoá index cũ hơn 14 ngày — chạy hằng ngày
docker compose exec elasticsearch curl -s -X DELETE \
  "localhost:9200/ar-chemistry-be-logs-$(date -d '14 days ago' +%Y.%m.%d)"
```

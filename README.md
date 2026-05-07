# AR Chemistry Backend

Backend cho hệ thống mô phỏng phản ứng hóa học AR.

## Công nghệ sử dụng

- Java 21
- Spring Boot
- PostgreSQL
- Docker
- JWT Authentication

---

# Clone project

```bash
git clone https://github.com/annguyen112233/ar_chemistry_be.git
```

---

# Chạy bằng Docker

## 1. Tạo file `.env`

```env
POSTGRES_DB=ar_chemistry
POSTGRES_USER=postgres
POSTGRES_PASSWORD=123456

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ar_chemistry
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=123456
```

---

## 2. Run docker compose

```bash
docker compose up --build
```

---

# API

## Base URL

```txt
http://localhost:8080
```

---

# Database

PostgreSQL chạy tại:

```txt
localhost:5432
```

---

# Team members

- Nguyen Hoai An
- Tran Dinh Bao
- Do Cao Dat
- Tran Thanh Nam

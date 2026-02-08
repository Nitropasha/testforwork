
# 📄 README – Document Service & Document Generator

Сервис реализует backend для работы с документами и отдельную утилиту генерации документов.  
Поддерживает создание документов, перевод по статусам, ведение истории, batch-обработку и фоновые воркеры.  

---

## 1️⃣ Структура проекта

| Модуль | Описание |
|--------|----------|
| `doc-service` | Основной микросервис, реализует CRUD опперации, batch submit/approve, поиск, реестр утверждений |
| `doc-generator` | Утилита генерации документов через REST API; реализует фоновые воркеры SUBMIT/APPROVE |
| `docker-compose.yml` | Поднимает PostgreSQL и сервисы |
| `doc-service/db/changelog` | Liquibase миграции (создание таблиц и индексов) |
| `EXPLAIN.md` | Пример поискового запроса, EXPLAIN (ANALYZE) и описание используемых индексов |

---

## 2️⃣ Запуск проекта

### 2.1 Docker

```bash
docker-compose up -d
```

- PostgreSQL: `localhost:5433`
- DB: `document_db`
- User: `document_user`
- Password: `document_pass`

---

### 2.2 Document Service

```bash
cd document-service
mvn clean spring-boot:run
```


---

### 2.3 Document Generator

```yaml
generator:
  enabled: true
  count: 1000
  batchSize: 100
  service-url: http://localhost:8080

worker:
  submit:
    delay-ms: 5000
    enabled: true
    batchSize: 10

  approve:
    delay-ms: 7000
    enabled: true
    batchSize: 10

```

Запуск:

```bash
cd document-generator
mvn clean spring-boot:run
```

- Генерация документов выполняется при старте  
- Воркеры работают в фоне и обрабатывают пачки документов  

---

## 3️⃣ API

### 3.1 Создание документа

```http
POST /documents
Content-Type: application/json

{
  "author": "Author-1",
  "title": "Document title"
}
```

Ответ: созданный документ.

---

### 3.2 Batch submit

```http
POST /documents/batch/submit
Content-Type: application/json

{
  "initiator": "worker",
  "comment": "auto-submit",
  "ids": [1,2,3,...]
}
```

Ответ: статус по каждому документу (`success`, `conflict`, `not_found`).

---

### 3.3 Batch approve

```http
POST /documents/batch/approve
Content-Type: application/json

{
  "initiator": "worker",
  "comment": "auto-approve",
  "ids": [1,2,3,...]
}
```

- При успешном утверждении создаётся запись в реестре  
- Если запись в реестр не создается — откат статуса

---

### 3.4 Поиск документов

```http
GET /documents/search?status=DRAFT&author=Author-1&dateFrom=2024-01-01&dateTo=2024-01-31&limit=100
```

- Фильтры: `status`, `author`, период по `createdAt`  
- Параметр `limit` используется для batch-обработки  
- Порядок результатов: `createdAt ASC`

---

### 3.5 Approve race (конкурентное утверждение)

```http
POST /documents/approve/race?documentId=1&threads=10&attempts=5
```

- Параллельные попытки утверждения документа  
- Результат:
  - сколько успешно  
  - сколько завершилось конфликтом  
  - финальный статус документа  

---

## 4️⃣ Базы данных

### 4.1 Таблицы

- **documents**: id, doc_number, author, title, status, version, created_at, updated_at  
- **document_status_history**: id, document_id, action, from_status, to_status, performed_by, performed_at, comment  
- **approval_registry**: id, document_id, approved_by, approved_at  

### 4.2 Индексы

- `idx_documents_status`  
- `idx_documents_author`  
- `idx_documents_created_at`  
- `idx_history_document_id`  

Используются для ускорения поиска, batch-обработки и сортировки по дате.

---

## 5️⃣ EXPLAIN

- Пример поискового запроса и EXPLAIN ANALYZE см. в `EXPLAIN.md`  
- Используемые индексы позволяют выполнять batch-поиск без full table scan  

---

## 6️⃣ Логирование

- Логи показывают:
  - количество документов
  - прогресс батчей
  - время выполнения операций
  - ошибки и откаты

Пример формата:

```
INFO SUBMIT-worker found 100 documents
INFO SUBMIT-worker finished in 123ms
```

---

## 7️⃣ Тесты

- Happy-path для одного документа  
- Batch submit  
- Batch approve с частичными результатами  
- Откат approve при ошибке записи в реестр  

---

## 8️⃣ Возможные улучшения

- Масштабирование batch-обработки на 5000+ документов  
- Вынесение реестра утверждений в отдельный сервис/БД  
- Расширение API пагинацией и сортировками  

---

## 9️⃣ Финальный запуск

```bash
docker-compose up -d
# Запуск document-service
cd document-service && mvn spring-boot:run
# Запуск document-generator с генератором и воркерами
cd document-generator && mvn spring-boot:run
```

- Генератор можно отключить: `generator.enabled=false`  
- Воркеры можно отключить: `worker.enabled=false`

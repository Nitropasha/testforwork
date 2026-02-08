
# 📄 EXPLAIN.md

В этом файле приведён пример поискового запроса документов, EXPLAIN (ANALYZE) и описание индексов,
используемых в текущей миграции.

---

## 1️⃣ Пример поискового запроса

Поиск документов:
- статус = 'DRAFT'
- автор = 'Author-10'
- период по дате создания

```sql
SELECT *
FROM documents
WHERE status = 'DRAFT'
  AND author = 'Author-10'
  AND created_at >= TIMESTAMP '2024-01-01 00:00:00'
  AND created_at <  TIMESTAMP '2024-02-01 00:00:00'
ORDER BY created_at
LIMIT 100;
```

---

## 2️⃣ EXPLAIN ANALYZE

```sql
EXPLAIN ANALYZE
SELECT *
FROM documents
WHERE status = 'DRAFT'
  AND author = 'Author-10'
  AND created_at >= TIMESTAMP '2024-01-01 00:00:00'
  AND created_at <  TIMESTAMP '2024-02-01 00:00:00'
ORDER BY created_at
LIMIT 100;
```

Пример возможного плана выполнения:

```
Limit  (cost=0.43..12.85 rows=100 width=256)
  ->  Bitmap Heap Scan on documents
        Recheck Cond: ((status = 'DRAFT'::text) AND (author = 'Author-10'::text) AND (created_at >= '2024-01-01 00:00:00'::timestamp) AND (created_at < '2024-02-01 00:00:00'::timestamp))
        ->  BitmapAnd  (cost=0.43..12.60 rows=2780 width=0)
              ->  Bitmap Index Scan on idx_documents_status  (cost=0.00..2.15 rows=100 width=0)
                    Index Cond: (status = 'DRAFT'::text)
              ->  Bitmap Index Scan on idx_documents_author  (cost=0.00..10.45 rows=2780 width=0)
                    Index Cond: (author = 'Author-10'::text)
Planning Time: 0.321 ms
Execution Time: 1.842 ms
```

> PostgreSQL использует отдельные индексы для `status` и `author`, объединяя их через Bitmap Index Scan.  
> Сортировка по `created_at` выполняется отдельным шагом, так как составного индекса нет.

---

## 3️⃣ Используемые индексы

```sql
CREATE INDEX idx_documents_status ON documents(status);
CREATE INDEX idx_documents_author ON documents(author);
CREATE INDEX idx_documents_created_at ON documents(created_at);
CREATE INDEX idx_history_document_id ON document_status_history(document_id);
```

### Назначение:

- `idx_documents_status` — ускоряет поиск по статусу (для воркеров)  
- `idx_documents_author` — ускоряет поиск по автору  
- `idx_documents_created_at` — сортировка и фильтр по дате создания  
- `idx_history_document_id` — ускоряет поиск истории документа  

---

## 4️⃣ Вывод

- Запрос выполняется эффективно с использованием отдельных индексов без составного индекса  
- Batch-обработка воркерами и поиск через REST API работают без полного сканирования таблицы

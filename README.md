# Anti-Plagiarism System

Система для анализа студенческих отчетов на плагиат и их статистической обработки.

## Архитектура системы

Система построена на микросервисной архитектуре и состоит из трех основных компонентов:

### 1. API Gateway (порт 8080)
- Отвечает за маршрутизацию запросов между сервисами
- Обрабатывает CORS и обеспечивает безопасность
- Предоставляет единую точку входа для клиентских приложений

### 2. File Storing Service (порт 8081)
- Управляет хранением и выдачей файлов
- Проверяет файлы на дубликаты с помощью хеширования
- Хранит метаданные файлов в PostgreSQL
- Обеспечивает физическое хранение файлов

### 3. File Analysis Service (порт 8082)
- Выполняет анализ текстовых файлов
- Подсчитывает статистику (абзацы, слова, символы)
- Сравнивает файлы на схожесть
- Хранит результаты анализа

## Технологический стек

- Java 17
- Spring Boot
- Spring Cloud Gateway
- PostgreSQL
- Maven
- Swagger/OpenAPI
- Lombok
- JPA/Hibernate

## API Спецификация

### File Storing Service

#### Загрузка файла
```
POST /api/v1/files/upload
Content-Type: multipart/form-data

Response:
{
    "fileId": Long,
    "fileName": String,
    "message": String,
    "isDuplicate": Boolean,
    "existingFileOriginalName": String
}
```

#### Получение файла
```
GET /api/v1/files/{fileId}
Response: File
```

#### Инициирование анализа
```
POST /api/v1/files/analyze
Content-Type: application/json

Request:
{
    "fileId": Long,
    "fileLocation": String
}
```

### File Analysis Service

#### Анализ файла
```
POST /api/v1/analysis/analyze
Content-Type: application/json

Request:
{
    "fileId": Long,
    "fileLocation": String
}

Response:
{
    "statistics": {
        "paragraphs": Integer,
        "words": Integer,
        "characters": Integer
    },
    "similarity": {
        "isDuplicate": Boolean,
        "similarityPercentage": Double
    }
}
```

## Установка и запуск

1. Требования:
   - Java 17
   - Maven
   - PostgreSQL

2. Настройка базы данных:
   ```sql
   CREATE DATABASE hse_antiplagiat;
   ```

3. Настройка конфигурации:
   - Обновите настройки в `application.yml` каждого сервиса
   - Укажите правильные учетные данные для базы данных

4. Сборка проекта:
   ```bash
   mvn clean package -DskipTests
   ```

5. Запуск сервисов:
   ```bash
   # API Gateway
   cd api-gateway
   mvn spring-boot:run

   # File Storing Service
   cd file-storing-service
   mvn spring-boot:run

   # File Analysis Service
   cd file-analysis-service
   mvn spring-boot:run
   ```

## Тестирование

1. Swagger UI доступен по адресам:
   - API Gateway: http://localhost:8080/swagger-ui.html
   - File Storing Service: http://localhost:8081/swagger-ui.html
   - File Analysis Service: http://localhost:8082/swagger-ui.html

2. Пример загрузки файла:
   ```bash
   curl -X POST \
     -H "Content-Type: multipart/form-data" \
     -F "file=@test_files/test1.txt" \
     http://localhost:8080/api/v1/files/upload
   ```

## Обработка ошибок

Система обрабатывает следующие типы ошибок:
- Ошибки загрузки файлов
- Ошибки доступа к базе данных
- Ошибки анализа файлов
- Ошибки маршрутизации
- Ошибки валидации

## Безопасность

- Валидация входных данных
- Проверка типов файлов
- Ограничение размера файлов
- Защита от переполнения буфера

## Мониторинг

- Логирование всех операций
- Отслеживание ошибок
- Мониторинг состояния сервисов
- Метрики производительности 
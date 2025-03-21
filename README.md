# Gateway Service

Този проект представлява фасадно (Gateway) Java/Spring Boot приложение, което:
1. Приема **JSON** и **XML** заявки.
2. Прави асинхронен REST call към [Postman Echo](https://postman-echo.com/post). Като в момента в application.properties симулираме 3 инстанции на (`OTHER_INTERNAL_SERVICE`):
   # Internal Service
     internal.service.urls.url1=https://postman-echo.com/post
     internal.service.urls.url2=https://postman-echo.com/post
     internal.service.urls.url3=https://postman-echo.com/post

3. Съхранява резултатите в PostgreSQL база данни.
4. Координира натоварването към `OTHER_INTERNAL_SERVICE` чрез Redis (използваме го за "load balancing" броячи).

## Какво използваме
- **Java 11**
- **Spring Boot**
- **PostgreSQL** за съхранение на данните (sessionId, requestId...).
- **Redis** за load balancing броячи.
- **Postman Echo** като пример за външна услуга (връща ни обратно payload-a).

## Контейнери

Стартирането на проекта в контейнери става чрез `docker` и `docker-compose`. 
В `docker-compose.yml` са дефинирани няколко контейнера:
- **db-postgres** – PostgreSQL база данни.
- **redis** – Redis за load balancing броячи.
- **gateway1** и **gateway2** – две инстанции на GatewayService, достъпни съответно на порт **8081** и **8082**.

## Стъпки за стартиране

1. mvn clean package
2. docker build -t gateway-service:latest .
3. docker compose up -d


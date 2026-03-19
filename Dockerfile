# Використовуємо офіційний образ з Maven + JDK 24 (твій release=24)
FROM maven:3.9.9-eclipse-temurin-24 AS builder

WORKDIR /app

# Копіюємо pom.xml і завантажуємо залежності (кешується)
COPY pom.xml .
RUN mvn dependency:go-offline

# Копіюємо весь код
COPY src ./src

# Збираємо проєкт
RUN mvn clean package -DskipTests

# Фінальний образ — легкий runtime
FROM eclipse-temurin:24-jre

WORKDIR /app

# Копіюємо тільки готовий JAR
COPY --from=builder /app/target/OnlinemanagerREST-1.0.0-all.jar app.jar

# Обидва файли монтуються через -v при запуску, дивись run-docker.sh:
#COPY ./onlinemanager.config.xml onlinemanager.config.xml
#COPY ./htpasswd.txt htpasswd.txt

# Запускаємо
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]



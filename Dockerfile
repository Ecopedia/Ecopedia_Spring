# 1. Use Eclipse Temurin base image
FROM eclipse-temurin:21-jdk-alpine

# 2. Set working directory
WORKDIR /app

# 3. Copy Gradle wrapper scripts and settings first
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
# (필요한 경우 build.gradle.kts, settings.gradle.kts도 포함)

# 4. 권한 부여 (gradlew가 실행되지 않는 오류 방지)
RUN chmod +x gradlew

# 5. Copy source code
COPY src src

# 6. Build the app (Spring Boot fat jar)
RUN ./gradlew clean build -x test

# 7. Run the app
CMD ["java", "-jar", "build/libs/app.jar"]
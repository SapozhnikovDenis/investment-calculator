# Инструкция по запуску
Для запуска приложения необходимо: 
1. перейти в директорию проекта
2. собрать и запустить приложение, выполнив команду в консоли:
```
./gradlew build && java -jar ./build/libs/investment-calculator.jar
```

Если после выполнения последней команды в консоле появилась строчка
```
Started InvestmentCalculatorApplication
```
То приложение успешно запущено.
Что бы остановить приложение нажмите Ctrl+C.

# Настройки
По стандарту приложение разворачивается на 8888 порту, но это значение можно моенять в application.yaml.
Также в application.yaml можно заменить токен и количество асинхронных потоков к сервису https://iexcloud.io/

# Решение задачи
Приложение умеет рассчитывать текущую стоимость портфеля (набора) акций и их распределение по секторам.
Для этого реализован сервис
```
POST /v1/stocks/cost/calculate
```
Пример запроса
```
curl -d '{"stocks": [ {"symbol": "AAPL","volume": 50} ] }' -H "Content-Type: application/json" -X POST http://localhost:8888/v1/stocks/cost/calculate
```
Пример ответа
```
{"value":12875,"allocations":[{"sector":"Electronic Technology","assetValue":12875,"proportion":100}]}
```

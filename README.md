# Лабораторна робота №1

## Варіант
18 — Метеостанція

## Призначення програми
Консольна Java-програма читає погодні спостереження з текстового файла, перевіряє коректність записів, пропускає помилкові рядки та формує підсумковий звіт.

## Формат вхідних даних
Кожний рядок має формат:

date;temperature;humidity;pressure;wind

Приклад:

2026-09-01;18.5;62.0;1013.2;3.4

## Результати
Програма обчислює:
- кількість коректних записів;
- мінімальну температуру;
- середню вологість;
- найбільшу швидкість вітру.

## Файл за замовчуванням
Вхід:
data/input.csv

Вихід:
out/report.txt

## Запуск

Компіляція:

javac -encoding UTF-8 -d target/classes src/main/java/ua/lpnu/kzp/Main.java

Звичайний запуск:

java -cp target/classes ua.lpnu.kzp.Main

Довідка:

java -cp target/classes ua.lpnu.kzp.Main --help

Явне задання вхідного і вихідного файла:

java -cp target/classes ua.lpnu.kzp.Main --input data/input.csv --output out/custom-report.txt


## Maven

Проєкт використовує Maven Wrapper, тому для збірки не потрібна окрема локальна версія Maven.

На macOS та Ubuntu замість `.\mvnw.cmd` використовується `./mvnw`.

Запуск тестів:

```powershell
.\mvnw.cmd test
```

Перевірка тестів і статичного аналізу SpotBugs:

```powershell
.\mvnw.cmd verify
```

Створення виконуваного JAR:

```powershell
.\mvnw.cmd package
```

Після успішної збірки виконуваний JAR створюється у каталозі `target`.

Запуск JAR:

```powershell
java -jar target/lab01-1.0.0.jar
```

Виведення довідки:

```powershell
java -jar target/lab01-1.0.0.jar --help
```

Виведення версії:

```powershell
java -jar target/lab01-1.0.0.jar --version
```

Поточна версія програми: `1.0.0`.

## CI

Для проєкту налаштовано GitHub Actions.

Під час `push` і `pull_request` проєкт автоматично перевіряється на:
- Ubuntu;
- Windows;
- macOS.

На кожній операційній системі використовується Java 21 та Maven Wrapper.

Команда `verify` запускає JUnit 5 тести та статичний аналіз SpotBugs.

Після успішної перевірки виконуваний JAR публікується як GitHub Actions artifact для кожної операційної системи.


## Лабораторна робота №2

Поточна версія для Lab 2: `2.0.0`.

У лабораторній роботі №2 рядкове представлення погодного запису
замінено класом `WeatherReading`.

Клас містить поля `date`, `temperature`, `humidity`, `pressure`, `wind`,
які зберігаються як `private final`.

Перевірка коректності значень виконується в конструкторі.
Для створення об'єкта з CSV-рядка використовується
статичний фабричний метод `WeatherReading.fromCsv(String line)`.

Для допоміжного незмінного значення використовується record
`TemperatureHumidity`.

У лабораторній роботі №2 також додано нові JUnit 5 тести
для конструктора `WeatherReading`, фабричного методу `fromCsv`
та record `TemperatureHumidity`.

Усі попередні тести лабораторної роботи №1 залишилися працездатними.

Після рефакторингу формат вхідного файла та підсумковий звіт
залишилися такими самими, як у лабораторній роботі №1.


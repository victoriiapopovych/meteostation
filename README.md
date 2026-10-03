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

У лабораторній роботі №2 виконано перехід від рядкового
представлення погодного запису до об'єктної моделі.

Основною сутністю є клас `WeatherReading` з полями:

- `date`;
- `temperature`;
- `humidity`;
- `pressure`;
- `wind`.

Поля зберігаються як `private final`.
Перевірка коректності значень виконується в конструкторі.

Для створення об'єкта з CSV-рядка використовується
статичний фабричний метод `WeatherReading.fromCsv(String line)`.

Для допоміжного незмінного значення використовується record
`TemperatureHumidity`.

### Порівняння Lab 1 і Lab 2

| Характеристика | Lab 1 | Lab 2 |
|---|---|---|
| Представлення запису | `String` і поля масиву | `WeatherReading` |
| Розбір CSV | у `Main` | `WeatherReading.fromCsv()` |
| Валідація | у циклі обробки | у конструкторі |
| Набір даних | рядки | `List<WeatherReading>` |
| Допоміжне значення | окремі змінні | `TemperatureHumidity` record |
| Незмінність сутності | не застосовувалась | `final` клас і `private final` поля |
| Builder | відсутній | `WeatherReading.Builder` |
| JUnit-тести | 9 | 23 загалом |
| Версія | `1.0.0` | `2.0.0` |
| JAR | `lab01-1.0.0.jar` для Lab 1 | `meteostation-2.0.0.jar` |

Після рефакторингу формат вхідного CSV та основні показники
підсумкового звіту залишилися сумісними з лабораторною роботою №1.

### Незмінність і Builder

Клас `WeatherReading` є незмінним:
він оголошений як `final`, усі поля мають модифікатор `private final`,
а setter-и відсутні.

Для зручного створення об'єктів реалізовано `WeatherReading.Builder`.
Метод `build()` викликає основний конструктор `WeatherReading`,
тому Builder не обходить правила валідації.

### Перевірка Lab 2

Запуск тестів:

```text
.\mvnw.cmd test


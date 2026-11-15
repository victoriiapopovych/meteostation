package ua.lpnu.kzp;

/**
 * Точка входу консольної програми «Метеостанція».
 */
public final class Main {

    private Main() {
    }

    /**
     * Запускає основний процес програми.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        new WeatherStationController().run(args);
    }
}
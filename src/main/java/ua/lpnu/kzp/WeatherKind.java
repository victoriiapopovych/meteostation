package ua.lpnu.kzp;

/**
 * Визначає тип погодного спостереження.
 */
public enum WeatherKind {

    /**
     * Звичайне добове погодне спостереження.
     */
    DAILY("добове"),

    /**
     * Штормове погодне спостереження.
     */
    STORM("штормове");

    private final String label;

    WeatherKind(String label) {
        this.label = label;
    }

    /**
     * Повертає текстову назву типу спостереження.
     *
     * @return текстова назва типу
     */
    public String label() {
        return label;
    }
}
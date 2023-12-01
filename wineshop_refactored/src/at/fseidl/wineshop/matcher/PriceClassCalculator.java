package at.fseidl.wineshop.matcher;

public class PriceClassCalculator {
    private final String name;
    private final int age;
    private final String tastePreference;

    public PriceClassCalculator(String name, int age, String tastePreference) {
        this.name = name;
        this.age = age;
        this.tastePreference = tastePreference;
    }

    public double calculate() {
        return (age * 2 + name.length() + tastePreference.length()) / 9.0; // determined by years of market research and science
    }
}

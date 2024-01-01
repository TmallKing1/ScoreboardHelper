package top.pigest.scoreboardhelper.config.property;

public abstract class BaseProperty<T> implements Property<T> {
    private final String key;
    private T value;
    private final T defaultValue;

    protected BaseProperty(String key, T defValue) {
        this.key = key;
        this.value = defValue;
        this.defaultValue = defValue;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }
}

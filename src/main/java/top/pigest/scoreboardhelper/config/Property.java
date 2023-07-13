package top.pigest.scoreboardhelper.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public interface Property<T> {
    String getKey();
    T getValue();
    T getDefaultValue();
    void setValue(T value);
    JsonElement toJson();
    void fromJson(JsonElement jsonElement);
    abstract class BaseProperty<T> implements Property<T> {
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

    class BooleanProperty extends BaseProperty<Boolean> {

        protected BooleanProperty(String key, Boolean defValue) {
            super(key, defValue);
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(getValue());
        }

        @Override
        public void fromJson(JsonElement jsonElement) {
            if(jsonElement.isJsonPrimitive()) {
                setValue(jsonElement.getAsBoolean());
            } else {
                throw new JsonParseException("Json must be a primitive.");
            }
        }
    }

    class IntegerProperty extends BaseProperty<Integer> {

        protected IntegerProperty(String key, Integer defValue) {
            super(key, defValue);
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(getValue());
        }

        @Override
        public void fromJson(JsonElement jsonElement) {
            if(jsonElement.isJsonPrimitive()) {
                setValue(jsonElement.getAsInt());
            } else {
                throw new JsonParseException("Json must be a primitive.");
            }
        }
    }

    class DoubleProperty extends BaseProperty<Double> {
        private final int digits;

        protected DoubleProperty(String key, Double defValue, int digits) {
            super(key, defValue);
            this.digits = digits;
        }

        @Override
        public void setValue(Double value) {
            double scale = Math.pow(10, digits);
            super.setValue(Math.round(value * scale) / scale);
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(getValue());
        }

        @Override
        public void fromJson(JsonElement jsonElement) {
            if(jsonElement.isJsonPrimitive()) {
                setValue(jsonElement.getAsDouble());
            } else {
                throw new JsonParseException("Json must be a primitive.");
            }
        }
    }
}

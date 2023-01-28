package hr.fer.zemris.java.custom.scripting.exec;

import java.util.function.BiFunction;

public class ValueWrapper {

    private Object value;

    public ValueWrapper(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void add(Object incValue) {
        performArithmeticalOperation(incValue, Double::sum, Integer::sum);
    }

    public void subtract(Object decValue) {
        performArithmeticalOperation(decValue, (n1, n2) -> n1 - n2, (n1, n2) -> n1 - n2);
    }

    public void multiply(Object mulValue) {
        performArithmeticalOperation(mulValue, (n1, n2) -> n1 * n2, (n1, n2) -> n1 * n2);
    }

    public void divide(Object divValue) {
        performArithmeticalOperation(divValue, (n1, n2) -> n1 / n2, (n1, n2) -> n1 / n2);
    }

    public int numCompare(Object withValue) {
        value = convertToIntOrDouble(value);
        withValue = convertToIntOrDouble(withValue);

        if (value instanceof Double && withValue instanceof Double) {
            return Double.compare((double) value, (double) withValue);
        } else if (value instanceof Integer && withValue instanceof Double) {
            return Double.compare((int) value, (double) withValue);
        } else if (value instanceof Double && withValue instanceof Integer) {
            return Double.compare((double) value, (int) withValue);
        } else {
            return Integer.compare((int) value, (int) withValue);
        }
    }

    private void performArithmeticalOperation(Object otherValue, BiFunction<Double, Double, Double> doubleFunction, BiFunction<Integer, Integer, Integer> intFunction) {
        value = convertToIntOrDouble(value);

        otherValue = convertToIntOrDouble(otherValue);

        if (value instanceof Double && otherValue instanceof Double) {
            value = doubleFunction.apply((double) value, (double) otherValue);
        } else if (value instanceof Integer && otherValue instanceof Double) {
            value = doubleFunction.apply((double) (int) value, (double) otherValue);
        } else if (value instanceof Double && otherValue instanceof Integer) {
            value = doubleFunction.apply((double) value, (double) (int) otherValue);
        } else {
            value = intFunction.apply((int) value, (int) otherValue);
        }
    }

    private Object convertToIntOrDouble(Object value) {
        if (value == null) {
            value = 0;
        } else if (value instanceof String) {
            if (((String) value).contains(".") || ((String) value).contains("E")) {
                try {
                    value = Double.parseDouble((String) value);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Result of operation is undefined.");
                }

            } else {
                try {
                    value = Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Result of operation is undefined.");
                }
            }
        }

        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

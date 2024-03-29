package org.example.ebs;

public class SubscriptionField {
    private String fieldName;
    private String operator;
    private Object value;

    public SubscriptionField(String fieldName, String operator, Object value) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        if (value instanceof String)
            return "(" + fieldName + ", " + operator + ", \"" + value + "\")";
        return "(" + fieldName + ", " + operator + ", " + value + ")";
    }
}

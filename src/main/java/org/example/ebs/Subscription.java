package org.example.ebs;

import java.util.ArrayList;
import java.util.List;

public class Subscription {
    private List<SubscriptionField> fields;

    public Subscription() {
        this.fields = new ArrayList<>();
    }

    public void addField(SubscriptionField field) {
        fields.add(field);
    }

    public List<SubscriptionField> getFields() {
        return fields;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (SubscriptionField field : fields) {
            sb.append(field.toString());
            sb.append("; ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("}");
        return sb.toString();
    }
}

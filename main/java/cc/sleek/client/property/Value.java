package cc.sleek.client.property;

public class Value<T> {

    private String name;
    private T value;
    private Visibility visibilityCheck;

    public Value(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public Value(String name, T value, Visibility visible) {
        this.name = name;
        this.value = value;
        visibilityCheck = visible;
    }

    public boolean isVisible() {
        if (visibilityCheck != null) {
            boolean check = visibilityCheck.check();
            return check;
        } else {
            return true;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Visibility getVisibilityCheck() {
        return visibilityCheck;
    }

    public void setVisibilityCheck(Visibility visibilityCheck) {
        this.visibilityCheck = visibilityCheck;
    }

    public interface Visibility {
        boolean check();
    }

}

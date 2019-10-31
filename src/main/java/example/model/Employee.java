package example.model;

import java.util.Arrays;
import java.util.List;

public class Employee {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee(int i, String s) {
        this.id = i;
        this.name = s;
    }

    public Employee() {
    }

    @Override
    public String toString() {
        return "[id=" + id + ", name=" + name + "]";
    }

    public static final List<Employee> employees = Arrays.asList(
            new Employee(1, "Raymond"),
            new Employee(2, "David"),
            new Employee(3, "Lisa"),
            new Employee(4, "Jason"),
            new Employee(5, "Jessica")
    );
}

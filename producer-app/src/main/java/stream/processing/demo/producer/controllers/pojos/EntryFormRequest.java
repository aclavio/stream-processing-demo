package stream.processing.demo.producer.controllers.pojos;

public class EntryFormRequest {
    private String id;
    private String firstName;
    private String lastName;
    private String portName;
    private String phone;
    private String notes;

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPortName() {
        return portName;
    }

    public String getPhone() { return phone; }

    public String getNotes() {
        return notes;
    }
}

package afester.javafx.examples.table.simple;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableRow {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty email;
    private final SimpleBooleanProperty flag;
    private final SimpleStringProperty comment;

    public TableRow(String firstName, String lastName, String eMail) {
        this(firstName, lastName, eMail, false, "");
    }
    
    
    public TableRow(String firstName, String lastName, String eMail, boolean flag, String comment) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(eMail);
        this.flag = new SimpleBooleanProperty(flag);
        this.comment = new SimpleStringProperty(comment);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String fName) {
        firstName.set(fName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String fName) {
        lastName.set(fName);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String fName) {
        email.set(fName);
    }

    // MANDATORY!!!!!!!
    public BooleanProperty flagProperty() {
        return this.flag;
    }

    public Boolean getFlag() {
        return flag.get();
    }

    public void setFlag(Boolean newValue) {
        flag.set(newValue);
    }


    // MANDATORY!!!!! If omitted, PropertyValueFactory<TableRow, String>("comment") 
    // will create a ReadOnlyObjectWrapper!!!!!!
    public StringProperty commentProperty() {
        return comment;
    }

    public String getComment() {
        return comment.get();
    }

    public void setComment(String value) {
        comment.set(value);
    }

    @Override
    public String toString() {
        String name = String.format("\"%s %s\"",  firstName.get(), lastName.get());
        String eMail = String.format("<%s>",  email.get());
        return String.format("%-20s %-30s %s %s", name, eMail, flag.get(), comment.get());
    }
}

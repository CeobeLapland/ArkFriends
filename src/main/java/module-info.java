module ceobe.arkfriends {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens ceobe.arkfriends to javafx.fxml;
    exports ceobe.arkfriends;
}
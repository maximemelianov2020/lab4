module org.example.lab4 {
    requires com.opencsv;
    requires javafx.controls;
    requires javafx.fxml;

    opens lab4 to javafx.fxml;
    opens lab4.model to javafx.fxml;
    opens lab4.csv to javafx.fxml;
    opens lab4.util to javafx.fxml;

    exports lab4;
    exports lab4.model;
    exports lab4.csv;
    exports lab4.util;
}

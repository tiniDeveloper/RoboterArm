module de.developup.roboterarm.roboterarm {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens de.developup.roboterarm.gui to javafx.fxml;
    exports de.developup.roboterarm.gui;
}
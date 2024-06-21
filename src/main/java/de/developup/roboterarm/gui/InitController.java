package de.developup.roboterarm.gui;

import de.developup.roboterarm.socket.ARPScanner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class InitController {
    @FXML
    private AnchorPane deviceListPane;
    @FXML
    ListView <Label> deviceListView;
    @FXML
    Button connectButton;
    String gewaehleHost;


    public void initialize() {


        deviceListView.setOnMouseClicked(event -> {
            Label selectedItem = deviceListView.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                // Hier können Sie definieren, was passieren soll, wenn ein Element angeklickt wird
                gewaehleHost=selectedItem.getText();
                System.out.println("Geklicktes Element: " + selectedItem.getText());
            }
        });

        deviceListView.setCellFactory(lv -> new ListCell<Label>() {
            @Override
            public void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getText());
                    setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-control-inner-background: #4d4d4d ;-fx-control-inner-background-alt: derive(-fx-control-inner-background, 50%);");
                }
            }
        });

        connectButton.setOnAction(event -> {
            FXMLLoader fxmlLoader  = new FXMLLoader(getClass().getResource("/de/developup/roboterarm/gui/GUIFenster.fxml"));

            try {

                Scene guiScene = new Scene(    fxmlLoader.load(), 1280, 720);
                GUIController guiController = fxmlLoader.getController();
                if(guiController.connect(gewaehleHost,9988)){
                    Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();

                    primaryStage.setScene(guiScene);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        Platform.runLater(this::filltheList);
    }

    private void filltheList() {
        ArrayList<ArrayList<String>> devices= ARPScanner.showDevices();
        for (ArrayList device : devices) {
            System.out.println(device.get(0));
            Label l= new Label();
            l.setText(device.get(1).toString());
            deviceListView.getItems().add(l);
        }
    }

}

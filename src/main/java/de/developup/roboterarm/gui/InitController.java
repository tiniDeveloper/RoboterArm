package de.developup.roboterarm.gui;

import de.developup.roboterarm.socket.ARPScanner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    @FXML
    TextArea statusArea;

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
                String connectionnMessage=guiController.connect(gewaehleHost,9988);
                if(connectionnMessage.equals("OK")){
                    Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();

                    primaryStage.setScene(guiScene);
                }else {
                    statusArea.setText(connectionnMessage);
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        });
        Platform.runLater(this::filltheList);
    }

    private void filltheList() {
        ArrayList<ArrayList<String>> devices= ARPScanner.showDevices();
        for (ArrayList device : devices) {
            Label l= new Label();
            l.setText(device.get(0).toString());
            deviceListView.getItems().add(l);
        }
    }

}

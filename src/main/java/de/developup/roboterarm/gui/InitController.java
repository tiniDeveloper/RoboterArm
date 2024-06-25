package de.developup.roboterarm.gui;

import de.developup.roboterarm.socket.ARPScanner;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class InitController {
    @FXML
    private ListView <Label> deviceListView;
    @FXML
    private Button connectButton;
    @FXML
    private Button bRefresh;


    @FXML
    private TextArea statusArea;

    private String gewaehleHost;

    /**
     *
     */
    public void initialize() {
        connectButton.setVisible(false);
        deviceListView.setOnMouseClicked(event -> {
            Label selectedItem = deviceListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                gewaehleHost=selectedItem.getText();
                connectButton.setVisible(true);
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
    }

    @FXML
    private void onConnectButtonClicked(Event event) {
        FXMLLoader fxmlLoader  = new FXMLLoader(getClass().getResource("/de/developup/roboterarm/gui/GUIFenster.fxml"));
        try {
            Scene guiScene = new Scene(    fxmlLoader.load(), 1280, 720);
            GUIController guiController = fxmlLoader.getController();
            String connectionnMessage=guiController.connect(gewaehleHost,9988);
            if(connectionnMessage.equals("OK")){
                Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
                primaryStage.setScene(guiScene);
            }else {
                statusArea.setText(connectionnMessage+"\n"+statusArea.getText());
            }
        } catch (IOException e) {
            statusArea.setText(e.getMessage());
        }
    }

    @FXML
    private void onRefreshButtonClicked(Event event) {
        ArrayList<ArrayList<String>> devices= ARPScanner.showDevices();
        deviceListView.getItems().clear();
        for (ArrayList<String> device : devices) {
            Label l= new Label();
            l.setText(device.get(0));
            deviceListView.getItems().add(l);
        }
    }

}

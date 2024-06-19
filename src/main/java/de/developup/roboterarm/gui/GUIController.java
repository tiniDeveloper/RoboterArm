package de.developup.roboterarm.gui;

import de.developup.roboterarm.socket.ClientHandler;
import de.developup.roboterarm.socket.ISocketMessageListiner;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * Klasse für die Steuerung der Benutzeroberfläche
 * Verarbeitung von Ein- und Ausgabe der GUI
 */
public class GUIController extends ISocketMessageListiner {
    ClientHandler clientHandler;
    @FXML
    HBox mHbox;
    byte[] data;

    @FXML
    public Circle outer;
    @FXML
    public Circle xyCircle;

    private double initialX;
    private double initialY;
    private double startX;
    private double startY;

    @FXML
    private  TextField fiField;
    @FXML
    Label lJoint1;
    @FXML
    Label lJoint2;
    @FXML
    Label lJoint3;
    @FXML
    Label lRotation;
    @FXML
    private TextField rField;
    @FXML
    private TextField hField;
    @FXML
    private TextField phiField;

    @FXML
    private Button bGoHome;
    @FXML
    private Button bMoveTo;

    /**
     * Konstruktor für die GUIController-Klasse.
     * Initialisiert das Daten-Array.
     */
    public GUIController() {
        data = new byte[9];
    }

    /**
     * Event-Handler für den "go Home" Button.
     * Sendet den Befehl zum Zurückfahren an den Server.
     *
     * @param event ActionEvent ausgelöst durch Button-Klick
     */
    @FXML
    void bGoHomeOnClick(ActionEvent event) {
        data[0]=(byte) 0x09;
        clientHandler.sendAndWaitForResponse(data);
    }

    /**
     * Event-Handler für den "Move To" Button.
     * Sendet die angegebenen Koordinaten an den Server und wartet auf eine Antwort.
     */
    @FXML
    void onbMoveToClick(){
        data[0]= (byte) 0x02;
        int r = Integer.parseInt(rField.getText());
        int h= Integer.parseInt(hField.getText());
        int phi = Integer.parseInt(phiField.getText());
        data[1]= (byte) (r&0xff);
        data[2]= (byte) ((r>>8)&0xff);
        data[3]= (byte) (h&0xff);
        data[4]= (byte) ((h>>8)&0xff);
        data[5]= (byte) (phi&0xff);
        data[6]= (byte) ((phi>>8)&0xff);
        clientHandler.sendAndWaitForResponse(data);
        int reconstructed = (data[1]&0xff)+((data[2]&0xff)<<8);
        if (reconstructed > 32767) {
            reconstructed -= 65536; // Subtract 2^16 to get the correct negative value
        }

        System.out.println("Data: "+reconstructed );

        //lJoint1.setText(String.valueOf((incommengByteArray[1]&0xff)+((incommengByteArray[2]&0xff)<<8)));
        //lJoint2.setText(String.valueOf((incommengByteArray[3]&0xff)+((incommengByteArray[4]&0xff)<<8)));
        //lJoint3.setText(String.valueOf((incommengByteArray[5]&0xff)+((incommengByteArray[6]&0xff)<<8)));
        //lRotation.setText(String.valueOf((incommengByteArray[7]&0xff)+((incommengByteArray[8]&0xff)<<8)));

        //Integer.parseInt(rField.getText().substring(2),16);

        //System.out.println("rField: "+ data[0]);
        //clientHandler.sendAndWaitForResponse(this.data);
    }

    /**
     * Initialisierungsmethode für den GUIController.
     */
    public void initialize() {

        startX = xyCircle.getLayoutX();
        startY = xyCircle.getLayoutY();

        // Set up number filtering
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("-?([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);

        xyCircle.setOnMousePressed(event -> {
            initialX = event.getSceneX() - xyCircle.getLayoutX();
            initialY = event.getSceneY() - xyCircle.getLayoutY();
        });

        xyCircle.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - initialX;
            double offsetY = event.getSceneY() - initialY;
            double radius = outer.getRadius() - xyCircle.getRadius();

            // Ensure the circle stays within the bounds of the outer circle
            double distance = Math.sqrt(Math.pow(offsetX - outer.getLayoutX(), 2) + Math.pow(offsetY - outer.getLayoutY(), 2));
            if (distance <= radius) {
                xyCircle.setLayoutX(offsetX);
                xyCircle.setLayoutY(offsetY);
            } else {
                double angle = Math.atan2(offsetY - outer.getLayoutY(), offsetX - outer.getLayoutX());
                xyCircle.setLayoutX(outer.getLayoutX() + radius * Math.cos(angle));
                xyCircle.setLayoutY(outer.getLayoutY() + radius * Math.sin(angle));
            }
            fiField.setText(String.valueOf(offsetX));
            rField.setText(String.valueOf(offsetY));
        });
        xyCircle.setOnMouseReleased(event -> {
            xyCircle.setLayoutX(startX);
            xyCircle.setLayoutY(startY);
        });

        Platform.runLater(() -> onConnecctionClosed("Please connect to the server!"));
    }

    /**
     * Verarbeitet eingehende Byte-Nachrichten und aktualisiert die GUI entsprechend.
     *
     * @param incommengByteArray das empfangene Byte-Array (Nachricht vom Server)
     */
    @Override
    public void onByteMessage(byte[] incommengByteArray) {
        switch (incommengByteArray[0]){
            case 0x10:// ist gestoppt
                System.out.println("ist gestoppt"+ incommengByteArray[0]);
                // daten anzeigen
                break;
            case 0x11:// aktuelle Daten empfangen
                lJoint1.setText(String.valueOf((incommengByteArray[1]&0xff)+((incommengByteArray[2]&0xff)<<8)));
                lJoint2.setText(String.valueOf((incommengByteArray[3]&0xff)+((incommengByteArray[4]&0xff)<<8)));
                lJoint3.setText(String.valueOf((incommengByteArray[5]&0xff)+((incommengByteArray[6]&0xff)<<8)));
                lRotation.setText(String.valueOf((incommengByteArray[7]&0xff)+((incommengByteArray[8]&0xff)<<8)));
                // daten anzeigen
                break;
            case 0x12:// nachricht melden
                lJoint1.setText(String.valueOf((incommengByteArray[1]&0xff)+((incommengByteArray[2]&0xff)<<8)));
                lJoint2.setText(String.valueOf((incommengByteArray[3]&0xff)+((incommengByteArray[4]&0xff)<<8)));
                lJoint3.setText(String.valueOf((incommengByteArray[5]&0xff)+((incommengByteArray[6]&0xff)<<8)));
                lRotation.setText(String.valueOf((incommengByteArray[7]&0xff)+((incommengByteArray[8]&0xff)<<8)));
                System.out.println("Fehlercode "+ incommengByteArray[0]);
                //data[0]= (byte) 0x19;
                //clientHandler.sendAndWaitForResponse(data);
                break;
            case 0x13:
                System.out.println("Unknown Byte Message13"+ incommengByteArray[0]);
                break;
            default:
                System.out.println("Unknown Byte Message"+ incommengByteArray[0]);
                break;
        }
    }

    /**
     * Bei Verbindungsabbruch wird Toast-Nachricht mit entstandenem Problem geöffnet
     *
     * @param reason Grund für Verbindungsabbruch
     */
    @Override
    public void onConnecctionClosed(String reason) {
        Stage stage = (Stage) rField.getScene().getWindow();
        Toast.showToast(stage, reason, "Connect", this::onToastButtonClick);
    }

    /**
     * Versucht Verbindung zum Server aufzubauen nach drücken von Connect Button
     *
     * @return true, wenn die Verbindung erfolgreich hergestellt wurde, andernfalls false
     */
    private boolean onToastButtonClick() {
        boolean istConnected=false;
        try {
            clientHandler= new ClientHandler("gui21", 9988, this);
            if(clientHandler.connected){
                istConnected=true;
            }

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return istConnected;
    }

}

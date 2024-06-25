package de.developup.roboterarm.gui;

import de.developup.roboterarm.socket.ClientHandler;
import de.developup.roboterarm.socket.ISocketMessageListiner;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.UnaryOperator;

/**
 * Klasse für die Steuerung der Benutzeroberfläche
 * Verarbeitung von Ein- und Ausgabe der GUI
 */
public class GUIController extends ISocketMessageListiner {

    private ClientHandler clientHandler;
    private boolean doorOpen;
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
    private TextArea statusArea;
    @FXML
    private Button bDisconnect;
    @FXML
    private Button bPickPlace;

    /**
     * Konstruktor für die GUIController-Klasse.
     * Initialisiert das Daten-Array.
     */
    public GUIController() {
        data = new byte[9];
    }

    public static class InvalidParameterException extends Exception {
        public InvalidParameterException(String message) {
            super(message);
        }
    }

    /**
     * Event-Handler für den "go Home" Button.
     * Sendet den Befehl zum Zurückfahren an den Server.
     */
    @FXML
    void bGoHomeOnClick() {
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

        try {
            int r = Integer.parseInt(rField.getText());
            int h= Integer.parseInt(hField.getText());
            int phi = Integer.parseInt(phiField.getText());
            if(checkInputs(r,h, phi)){
                data[1]= (byte) (r&0xff);
                data[2]= (byte) ((r>>8)&0xff);
                data[3]= (byte) (h&0xff);
                data[4]= (byte) ((h>>8)&0xff);
                data[5]= (byte) (phi&0xff);
                data[6]= (byte) ((phi>>8)&0xff);
                clientHandler.sendAndWaitForResponse(data);
            }
        } catch (InvalidParameterException e) {
            statusArea.setText(e.getMessage()+statusArea.getText());
            statusArea.setPrefColumnCount(5);
        } catch (NumberFormatException e) {
            statusArea.setText("bitte geben Sie eine gültigen Nummer ein: "+e.getMessage()+"\n"+statusArea.getText());
        }



//        int reconstructed = (data[1]&0xff)+((data[2]&0xff)<<8);
//        if (reconstructed > 32767) {
//            reconstructed -= 65536; // Subtract 2^16 to get the correct negative value
//        }
//
//        System.out.println("Data: "+reconstructed );

        //lJoint1.setText(String.valueOf((incommengByteArray[1]&0xff)+((incommengByteArray[2]&0xff)<<8)));
        //lJoint2.setText(String.valueOf((incommengByteArray[3]&0xff)+((incommengByteArray[4]&0xff)<<8)));
        //lJoint3.setText(String.valueOf((incommengByteArray[5]&0xff)+((incommengByteArray[6]&0xff)<<8)));
        //lRotation.setText(String.valueOf((incommengByteArray[7]&0xff)+((incommengByteArray[8]&0xff)<<8)));

        //Integer.parseInt(rField.getText().substring(2),16);

        //System.out.println("rField: "+ data[0]);
        //clientHandler.sendAndWaitForResponse(this.data);
    }
    private boolean checkInputs(int r,int h, int phi) throws InvalidParameterException {
        double magnet_offset = 65 - 34;
        if (r <= 0 || h <= 0) throw new InvalidParameterException("Die Werte für 'r' und 'h' müssen positiv sein."+"\n");
        double hypotenuse = Math.sqrt(r * r + (h - magnet_offset) * (h - magnet_offset));

        if (hypotenuse == 0) throw new InvalidParameterException("Die Hypotenuse darf nicht Null sein."+"\n");
        if(phi <-90||phi>90)
            throw new InvalidParameterException("Der Winkel Phi soll im Breich von -90 bis 90 grad liegen."+"\n");
        return true;
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
        });
        xyCircle.setOnMouseReleased(event -> {
            xyCircle.setLayoutX(startX);
            xyCircle.setLayoutY(startY);
        });
    }

    @FXML
    void bPickPlaceOnClick() {
        doorOpen=false;
        data[0]=(byte) 0x07;
        clientHandler.sendAndWaitForResponse(data);
        doorOpen=true;
    }


    @FXML
    void bDisconnectOnClick(Event event) {
        try {
            clientHandler.closeConnection();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        FXMLLoader initPaneloader = new FXMLLoader(getClass().getResource("/de/developup/roboterarm/gui/Init_Fanster.fxml"));
        try {
            Scene InitScene = new Scene(initPaneloader.load(), 732, 720);
            Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();

            primaryStage.setScene(InitScene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                System.out.println(" ist gemacht"+ incommengByteArray[0]);
                // daten anzeigen
                break;
            case 0x11:// aktuelle Daten empfangen
                System.out.println("aktuelle daten empfangen");
                int[]x=bytesToInt(incommengByteArray);
                lJoint1.setText(String.valueOf(x[0]));
                lJoint2.setText(String.valueOf(x[1]));
                lJoint3.setText(String.valueOf(x[2]));
                lRotation.setText(String.valueOf(x[3]));
//                lJoint1.setText(String.valueOf((incommengByteArray[1]&0xff)+((incommengByteArray[2]&0xff)<<8)));
//                lJoint2.setText(String.valueOf((incommengByteArray[3]&0xff)+((incommengByteArray[4]&0xff)<<8)));
//                lJoint3.setText(String.valueOf((incommengByteArray[5]&0xff)+((incommengByteArray[6]&0xff)<<8)));
//                lRotation.setText(String.valueOf((incommengByteArray[7]&0xff)+((incommengByteArray[8]&0xff)<<8)));
                // daten anzeigen
                break;
            case 0x12:// nachricht melden
                lJoint1.setText(String.valueOf((incommengByteArray[1]&0xff)+((incommengByteArray[2]&0xff)<<8)));
                lJoint2.setText(String.valueOf((incommengByteArray[3]&0xff)+((incommengByteArray[4]&0xff)<<8)));
                lJoint3.setText(String.valueOf((incommengByteArray[5]&0xff)+((incommengByteArray[6]&0xff)<<8)));
                lRotation.setText(String.valueOf((incommengByteArray[7]&0xff)+((incommengByteArray[8]&0xff)<<8)));
                System.out.println("Message-code: "+ incommengByteArray[0]);
                //data[0]= (byte) 0x19;
                //clientHandler.sendAndWaitForResponse(data);
                break;
            default:
                System.out.println("Unknown Byte Message: "+ incommengByteArray[0]);
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
        System.out.println("Connection closed");
        //Stage stage = (Stage) rField.getScene().getWindow();
        //Toast.showToast(stage, reason, "Connect", this::onToastButtonClick);
    }

    /**
     * Versucht Verbindung zum Server aufzubauen nach drücken von Connect Button
     *
     * @param host: host von dem Server zum Verbinden
     * @param port: port des Servers
     * @return true, wenn die Verbindung erfolgreich hergestellt wurde, andernfalls false
     */
    public String connect(String host, int port) {
        String message ="";
        try {
            clientHandler= new ClientHandler(host, port, this);
            if(clientHandler.connected){
                message="OK";
            }

        }catch (IOException e){
            message=e.getMessage();
        }
        return message;
    }

    int[] bytesToInt(byte[] incommmingData){
        int[] x= new int[4];
        if(((incommmingData[1]&0xff)+((incommmingData[2]&0xff)<<8)>32767))
            x[0]=((incommmingData[1]&0xff)+((incommmingData[2]&0xff)<<8)-65536);
        else
            x[0]=(incommmingData[1]&0xff)+((incommmingData[2]&0xff)<<8);

        if(((incommmingData[3]&0xff)+((incommmingData[4]&0xff)<<8)>32767))
            x[1]=((incommmingData[3]&0xff)+((incommmingData[4]&0xff)<<8)-65536);
        else
            x[1]=(incommmingData[3]&0xff)+((incommmingData[4]&0xff)<<8);

        if(((incommmingData[5]&0xff)+((incommmingData[6]&0xff)<<8)>32767))
            x[2]=((incommmingData[5]&0xff)+((incommmingData[6]&0xff)<<8)-65536);
        else
            x[2]=(incommmingData[5]&0xff)+((incommmingData[6]&0xff)<<8);

        if(((incommmingData[7]&0xff)+((incommmingData[8]&0xff)<<8)>32767))
            x[3]=((incommmingData[7]&0xff)+((incommmingData[8]&0xff)<<8)-65536);
        else
            x[3]=(incommmingData[7]&0xff)+((incommmingData[8]&0xff)<<8);


        return x;
    }


}

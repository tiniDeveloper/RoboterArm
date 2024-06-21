package de.developup.roboterarm.gui;

import de.developup.roboterarm.socket.ARPScanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
/**
 *
 * Klasse zum Starten der Benutzeroberfläche für das Ansteuern des Roboterarmes
 *
 */
public class MainKlass extends Application  {

    /**
     * Diese Methode startet die Benutzeroberfläche zum Steuern des Roboterarms.
     *
     * @param stage Objekt, welches die Benutzeroberfläche beinhaltet.
     * @throws IOException wenn die FXML-Datei nicht geladen werden kann.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader initPaneloader = new FXMLLoader(getClass().getResource("/de/developup/roboterarm/gui/Init_Fanster.fxml"));
        Scene InitScene = new Scene(initPaneloader.load(), 1280, 720);

        FXMLLoader fxmlLoader  = new FXMLLoader(getClass().getResource("/de/developup/roboterarm/gui/GUIFenster.fxml"));
        Scene guiScene   = new Scene(    fxmlLoader.load(), 1280, 720);

        stage.setTitle("Roboter Arm Client");
        stage.setResizable(false);
        stage.setScene(InitScene);
        stage.show();
    }

    /**
     * Hauptmethode zum Starten der JavaFX-Anwendung durch die Methode launch()
     *
     * @param args mögliche Argumente (Nicht verwendet)
     */
    public static void main(String[] args) {
        launch();
    }


}
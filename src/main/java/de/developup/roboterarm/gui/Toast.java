package de.developup.roboterarm.gui;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


/**
 * Klasse zum ausgrauen der Benutzeroberfläche vor dem connecten zum Server
 */
public class Toast {
    private static Stage toastStage;

    /**
     * Zeigt eine Toast-Nachricht an.
     *
     * @param ownerStage die Benutzeroberfläche, die das Toast-Fenster besitzt.
     * @param message die Nachricht, die im Toast angezeigt wird.
     * @param buttonText der Text des Buttons im Toast.
     * @param onToastButtonClick das Interface, das das drücken des Buttons behandelt.
     */
    public static void showToast(Stage ownerStage, String message, String buttonText, OnToastButtonClick onToastButtonClick) {
        toastStage =new Stage();
        toastStage.initStyle(StageStyle.UNDECORATED);
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Label toastLabel = new Label(message);
        toastLabel.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.75); -fx-text-fill: white; -fx-padding: 10px;");
        toastLabel.setLayoutX(500);
        toastLabel.setLayoutY(200);
        toastLabel.setTextFill(Color.WHITE);

        Button toastButton = new Button(buttonText);
        toastButton.setOnAction(e -> {
            if (onToastButtonClick.onClick()) {
                toastStage.close();
            }
        });

        HBox root = new HBox(10, toastLabel, toastButton);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-padding: 10px; -fx-background-radius: 20;");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(null);
        toastStage.setScene(scene);

        toastStage.setWidth(ownerStage.getWidth()-14);
        toastStage.setHeight(ownerStage.getHeight()-38);
        //toastStage.setX(ownerStage.getX()+40);
        //toastStage.setY(ownerStage.getY()+30);

        updateToastPosition(ownerStage, toastStage);
        ownerStage.xProperty().addListener((obs, oldVal, newVal) -> updateToastPosition(ownerStage, toastStage));
        ownerStage.yProperty().addListener((obs, oldVal, newVal) -> updateToastPosition(ownerStage, toastStage));

        toastStage.show();

        FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), root);
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(1);
        fadeInTransition.setCycleCount(1);

        fadeInTransition.play();
    }

    /**
     * Aktualisiert die Position des Toast-Fensters.
     *
     * @param ownerStage die Benutzeroberfläche, die das Toast-Fenster besitzt.
     * @param toastStage das Toast-Fenster, dessen Position aktualisiert wird.
     */
    private static void updateToastPosition(Stage ownerStage, Stage toastStage) {
        toastStage.setX(ownerStage.getX()+7);
        toastStage.setY(ownerStage.getY()+31);
    }

    /**
     * Funktionales Interface für die Behandlung des Knopf drücken im Toast-Fenster.
     */
    @FunctionalInterface
    public interface OnToastButtonClick {
        boolean onClick();
    }

}

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

public class Toast {
    private static Stage toastStage;
    public static void showToast(Stage ownerStage, String message, String buttonText, OnToastButtonClick onToastButtonClick) {
        toastStage =new Stage();
        toastStage.initStyle(StageStyle.UNDECORATED);
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Label toastLabel = new Label(message);
        toastLabel.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.75); -fx-text-fill: white; -fx-padding: 10px;");
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

        toastStage.setWidth(ownerStage.getWidth()-9);
        toastStage.setHeight(ownerStage.getHeight()-40);
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
    private static void updateToastPosition(Stage ownerStage, Stage toastStage) {
        toastStage.setX(ownerStage.getX()+7);
        toastStage.setY(ownerStage.getY()+40);
    }
    @FunctionalInterface
    public interface OnToastButtonClick {
        boolean onClick();
    }

}

package de.developup.roboterarm.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GUIControllerTest {

    @Test
    void checkInputs() {
        GUIController GuiCheck = new GUIController();

        // r darf nicht kleiner gleich null sein
        assertThrows(GUIController.InvalidParameterException.class, () -> {
            GuiCheck.checkInputs(0,5,5);
        });
        assertThrows(GUIController.InvalidParameterException.class, () -> {
            GuiCheck.checkInputs(-10,5,5);
        });

        // h darf nicht kleiner gleich null sein
        assertThrows(GUIController.InvalidParameterException.class, () -> {
            GuiCheck.checkInputs(5,0,5);
        });
        assertThrows(GUIController.InvalidParameterException.class, () -> {
            GuiCheck.checkInputs(5,-10,5);
        });

        // Winkel darf nicht [-90:90] überschreiten
        assertThrows(GUIController.InvalidParameterException.class, () -> {
            GuiCheck.checkInputs(5,5,-91);
        });
        assertThrows(GUIController.InvalidParameterException.class, () -> {
            GuiCheck.checkInputs(5,5,91);
        });
    }
}

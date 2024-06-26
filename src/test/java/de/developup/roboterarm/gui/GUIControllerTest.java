package de.developup.roboterarm.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GUIControllerTest {

    @Test
    void checkInputs() {
        GUIController GuiCheck = new GUIController();

        assertThrows(GUIController.InvalidParameterException.class, () -> {
            GuiCheck.checkInputs(0,0,0);
        });
        assertThrows(GUIController.InvalidParameterException.class, () -> {
            GuiCheck.checkInputs(-10,10,10);
        });
    }
}

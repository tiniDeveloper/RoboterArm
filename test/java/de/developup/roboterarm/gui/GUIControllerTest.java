package de.developup.roboterarm.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasse zum Testen von wichtigen Methoden.
 */
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

    @Test
    void bytesToInt(){
        GUIController GuiCheck = new GUIController();


        byte[] test = new byte[] {0,(byte)0x2C,(byte)0x1,(byte)0xD4,(byte)0xFE,(byte)0xF4,(byte)0x1,(byte)0xEC,(byte)0xFF};

        int[] result = new int[] {300,-300,500,-20};

        int[] actual = GuiCheck.bytesToInt(test);

        assertEquals(result[0], actual[0]);
        assertEquals(result[1], actual[1]);
        assertEquals(result[2], actual[2]);
        assertEquals(result[3], actual[3]);



    }
}

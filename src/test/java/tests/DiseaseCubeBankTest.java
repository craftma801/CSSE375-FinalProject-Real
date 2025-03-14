package tests;

import main.ActionFailedException;
import main.CityColor;
import main.DiseaseCubeBank;
import main.InvalidColorException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DiseaseCubeBankTest {
    @Test
    public void testCityInfectedInvalid() {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        assertThrows(InvalidColorException.class, () -> diseaseCubeBank.cityInfected(CityColor.EVENT_COLOR));
    }

    @Test
    public void testColorTreatedInvalid() {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        assertThrows(InvalidColorException.class, () -> diseaseCubeBank.colorTreated(CityColor.EVENT_COLOR));
    }

    @Test
    public void testRemainingCubesInvalid() {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        assertEquals(-1, diseaseCubeBank.remainingCubes(CityColor.EVENT_COLOR));
    }
}

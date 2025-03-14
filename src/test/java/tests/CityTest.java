package tests;

import main.Point;
import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class CityTest {
    private City testCity;
    private OutbreakManager outbreakManager;

    @BeforeEach
    void setUp() {
        this.testCity = new City("Terre Haute", new Point(40,70), CityColor.YELLOW);
        this.outbreakManager = new OutbreakManager(new DummyGameWindow());
    }

    @Test
    public void testNotInfectedRed(){
        assertEquals(0,this.testCity.getInfectionLevel(CityColor.RED));
    }

    @Test
    public void testInfectionLevelOneRed(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        this.testCity.infect(CityColor.RED, diseaseCubeBank, outbreakManager);
        assertEquals(1,this.testCity.getInfectionLevel(CityColor.RED));
    }

    @Test
    public void testInfectPastThreeRed(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        this.testCity.infect(CityColor.RED,diseaseCubeBank, outbreakManager);
        this.testCity.infect(CityColor.RED,diseaseCubeBank, outbreakManager);
        this.testCity.infect(CityColor.RED,diseaseCubeBank, outbreakManager);
        assertEquals(3,this.testCity.getInfectionLevel(CityColor.RED));
    }

    @Test
    public void testOutbreaksActivate(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
        atlanta.infect(CityColor.BLUE, diseaseCubeBank, outbreakManager);
        atlanta.infect(CityColor.BLUE, diseaseCubeBank, outbreakManager);
        atlanta.infect(CityColor.BLUE, diseaseCubeBank, outbreakManager);
        atlanta.infect(CityColor.BLUE, diseaseCubeBank, outbreakManager);
        assertEquals(1,outbreakManager.getOutbreaks());
        assertEquals(3,atlanta.getInfectionLevel(CityColor.BLUE));
    }

    @Test
    public void testOutbreaksChain(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        City taipei = new City("Taipei", new Point(0, 0), CityColor.RED);
        City hongKong = new City("Hong Kong", new Point(0, 0), CityColor.RED);

        taipei.addConnection(hongKong);
        hongKong.addConnection(taipei);

        taipei.infectRed(diseaseCubeBank, outbreakManager);
        taipei.infectRed(diseaseCubeBank, outbreakManager);
        taipei.infectRed(diseaseCubeBank, outbreakManager);
        taipei.infectRed(diseaseCubeBank, outbreakManager);

        taipei.outbreakIsHappening = false;
        hongKong.outbreakIsHappening = false;

        hongKong.infectRed(diseaseCubeBank, outbreakManager);
        hongKong.infectRed(diseaseCubeBank, outbreakManager);
        hongKong.infectRed(diseaseCubeBank, outbreakManager);

        assertEquals(3, outbreakManager.getOutbreaks());
    }

    @Test
    public void testOutbreaksDontChainOnSelf(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        City baghdad =new City("Baghdad", new Point(0, 0), CityColor.BLACK);
        City istanbul = new City("Istanbul", new Point(0, 0), CityColor.BLACK);

        baghdad.addConnection(istanbul);
        istanbul.addConnection(baghdad);

        baghdad.infectBlack(diseaseCubeBank, outbreakManager);
        baghdad.infectBlack(diseaseCubeBank, outbreakManager);
        baghdad.infectBlack(diseaseCubeBank, outbreakManager);
        baghdad.infectBlack(diseaseCubeBank, outbreakManager);

        istanbul.infectBlack(diseaseCubeBank, outbreakManager);
        istanbul.infectBlack(diseaseCubeBank, outbreakManager);
        istanbul.infectBlack(diseaseCubeBank, outbreakManager);

        assertEquals(2, outbreakManager.getOutbreaks());
    }

    @Test
    public void testCreateFakeCity(){
        assertEquals("Terre Haute", this.testCity.name);
        assertEquals(CityColor.YELLOW, this.testCity.defaultColor());
    }

    @Test
    public void testTwoCities(){
        City secondCity = new City("San Diego", new Point(20, 100), CityColor.BLUE);
        assertEquals("San Diego",secondCity.name);
        assertEquals(CityColor.BLUE,secondCity.defaultColor());
    }

    @Test
    public void hasNoResearchStation(){
        assertFalse(this.testCity.hasResearchStation());
    }

    @Test
    public void hasResearchStation(){
        City secondCity = new City("San Diego", new Point(20, 100), CityColor.BLUE);
        secondCity.buildResearchStation();
        assertTrue(secondCity.hasResearchStation());
    }

    @Test
    public void testAddPawn(){
        Player player = new Player(Color.RED, this.testCity);
        this.testCity.addPawn(player);
        assert(!this.testCity.players.isEmpty());
    }

    @Test
    public void testTreatDisease(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        this.testCity.infect(CityColor.YELLOW,diseaseCubeBank, outbreakManager);
        this.testCity.treatDisease(CityColor.YELLOW,diseaseCubeBank);
        assertEquals(0,this.testCity.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testTreatDisease2(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        this.testCity.infect(CityColor.YELLOW,diseaseCubeBank, outbreakManager);
        this.testCity.infect(CityColor.YELLOW,diseaseCubeBank, outbreakManager);
        assertEquals(22,diseaseCubeBank.remainingCubes(CityColor.YELLOW));
        this.testCity.treatDisease(CityColor.YELLOW,diseaseCubeBank);
        assertEquals(1,this.testCity.getInfectionLevel(CityColor.YELLOW));
        assertEquals(23,diseaseCubeBank.remainingCubes(CityColor.YELLOW));
    }

    @Test
    public void testTreatDiseaseNoCubes(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        this.testCity.treatDisease(CityColor.YELLOW,diseaseCubeBank);
        assertEquals(0,this.testCity.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testMedicTreatDisease(){
        testMedicTreatDiseaseHelper(CityColor.YELLOW);
        testMedicTreatDiseaseHelper(CityColor.BLUE);
        testMedicTreatDiseaseHelper(CityColor.BLACK);
        testMedicTreatDiseaseHelper(CityColor.RED);
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        assertThrows(InvalidColorException.class, () ->testCity.medicTreatDisease(CityColor.EVENT_COLOR, diseaseCubeBank));
    }

    public void testMedicTreatDiseaseHelper(CityColor color){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        this.testCity.infect(color,diseaseCubeBank, outbreakManager);
        this.testCity.infect(color,diseaseCubeBank, outbreakManager);
        this.testCity.infect(color,diseaseCubeBank, outbreakManager);
        assertEquals(21,diseaseCubeBank.remainingCubes(color));
        this.testCity.medicTreatDisease(color,diseaseCubeBank);
        assertEquals(0,this.testCity.getInfectionLevel(color));
        assertEquals(24,diseaseCubeBank.remainingCubes(color));
    }
}

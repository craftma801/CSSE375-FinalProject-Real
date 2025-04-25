package tests;

import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class CityTest {
    private City testCity;
    private OutbreakManager outbreakManager;

    // The following helper methods are used to reduce code duplication in these tests.
    private void infectCityNTimes(DiseaseCubeBank dcb, City city, CityColor color, int times){
        for (int i = 0; i < times; i++) {
            dcb.infectCity(city, color, outbreakManager);
        }
    }

    private void connectCities(City a, City b) {
        a.addConnection(b);
        b.addConnection(a);
    }

    private void resetOutbreakFlags(City... cities) {
        for (City c : cities) {
            c.outbreakIsHappening = false;
        }
    }

    @BeforeEach
    void setUp() {
        this.testCity = new City("Terre Haute", 40,70, CityColor.YELLOW);
        this.outbreakManager = new OutbreakManager(new DummyGameWindow());
    }

    @Test
    public void testNotInfectedRed(){
        assertEquals(0,this.testCity.getInfectionLevel(CityColor.RED));
    }

    @Test
    public void testInfectionLevelOneRed(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        infectCityNTimes(diseaseCubeBank, testCity, CityColor.RED, 1);
        assertEquals(1,this.testCity.getInfectionLevel(CityColor.RED));
    }

    @Test
    public void testInfectPastThreeRed(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();

        infectCityNTimes(diseaseCubeBank, testCity, CityColor.RED, 3);
        assertEquals(3,this.testCity.getInfectionLevel(CityColor.RED));
    }

    @Test
    public void testOutbreaksActivate(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        City atlanta = new City("Atlanta", 0, 0, CityColor.BLUE);

        infectCityNTimes(diseaseCubeBank, atlanta, atlanta.color, 4);
        assertEquals(1,outbreakManager.getOutbreaks());
        assertEquals(3,atlanta.getInfectionLevel(CityColor.BLUE));
    }

    @Test
    public void testOutbreaksChain(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        City taipei = new City("Taipei", 0, 0, CityColor.RED);
        City hongKong = new City("Hong Kong", 0, 0, CityColor.RED);
        connectCities(taipei, hongKong);

        infectCityNTimes(diseaseCubeBank, taipei, CityColor.RED, 4);
        resetOutbreakFlags(taipei, hongKong);
        infectCityNTimes(diseaseCubeBank, hongKong, CityColor.RED, 3);

        assertEquals(3, outbreakManager.getOutbreaks());
    }

    @Test
    public void testOutbreaksDontChainOnSelf(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        City baghdad =new City("Baghdad", 0, 0, CityColor.BLACK);
        City istanbul = new City("Istanbul", 0, 0, CityColor.BLACK);
        connectCities(baghdad, istanbul);
        infectCityNTimes(diseaseCubeBank, baghdad, baghdad.color, 4);
        infectCityNTimes(diseaseCubeBank, istanbul, istanbul.color, 3);

        assertEquals(2, outbreakManager.getOutbreaks());
    }

    @Test
    public void testCreateFakeCity(){
        assertEquals("Terre Haute", this.testCity.name);
        assertEquals(CityColor.YELLOW, this.testCity.defaultColor());
    }

    @Test
    public void testTwoCities(){
        City secondCity = new City("San Diego", 20, 100, CityColor.BLUE);
        assertEquals("San Diego",secondCity.name);
        assertEquals(CityColor.BLUE,secondCity.defaultColor());
    }

    @Test
    public void hasNoResearchStation(){
        assertFalse(this.testCity.hasResearchStation());
    }

    @Test
    public void hasResearchStation(){
        City secondCity = new City("San Diego", 20, 100, CityColor.BLUE);
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
        infectCityNTimes(diseaseCubeBank, this.testCity, CityColor.YELLOW, 1);
        this.testCity.treatDisease(CityColor.YELLOW,diseaseCubeBank);
        assertEquals(0,this.testCity.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testTreatDiseaseDecreasesInfectionLevel() {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        infectCityNTimes(diseaseCubeBank, testCity, CityColor.YELLOW, 2);
        testCity.treatDisease(CityColor.YELLOW, diseaseCubeBank);
        assertEquals(1, testCity.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testTreatDiseaseReturnsCubeToBank() {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        infectCityNTimes(diseaseCubeBank, testCity, CityColor.YELLOW, 2);
        assertEquals(22, diseaseCubeBank.remainingCubes(CityColor.YELLOW));
        testCity.treatDisease(CityColor.YELLOW, diseaseCubeBank);
        assertEquals(23, diseaseCubeBank.remainingCubes(CityColor.YELLOW));
    }

    @Test
    public void testTreatDiseaseNoCubes(){
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        this.testCity.treatDisease(CityColor.YELLOW,diseaseCubeBank);
        assertEquals(0,this.testCity.getInfectionLevel(CityColor.YELLOW));
    }

// The below parameterized tests eliminate the too many asserts from testMedicTreatDisease and its helper method.
    @ParameterizedTest
    @EnumSource(value = CityColor.class, names = { "RED", "BLUE", "BLACK", "YELLOW" })
    public void testMedicRemovesAllCubes_Param(CityColor color) {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        infectCityNTimes(diseaseCubeBank, testCity, color, 3);
        testCity.medicTreatDisease(color, diseaseCubeBank);
        assertEquals(0, testCity.getInfectionLevel(color));
    }

    @ParameterizedTest
    @EnumSource(value = CityColor.class, names = { "RED", "BLUE", "BLACK", "YELLOW" })
    public void testMedicReturnsCubesToBank_Param(CityColor color) {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        infectCityNTimes(diseaseCubeBank, testCity, color, 3);
        assertEquals(21, diseaseCubeBank.remainingCubes(color));
        testCity.medicTreatDisease(color, diseaseCubeBank);
        assertEquals(24, diseaseCubeBank.remainingCubes(color));
    }
}

package tests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import main.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CitySteps {
    public static City testCity;
    public static City testCity2;
    DiseaseCubeBank diseaseCubeBank;
    OutbreakManager outbreakManager;

    private CityColor stringToEnum(String color) {
        return switch (color) {
            case "yellow" -> CityColor.YELLOW;
            case "red" -> CityColor.RED;
            case "black" -> CityColor.BLACK;
            case "blue" -> CityColor.BLUE;
            default -> CityColor.EVENT_COLOR;
        };
    }

    @Given("A city {string} with color {string}")
    public void given_a_city(String name, String color) {
        diseaseCubeBank = new DiseaseCubeBank();
        outbreakManager = new OutbreakManager(new DummyGameWindow());
//        Point cityLocation = new Point(0, 0);
        CityColor cityColor = stringToEnum(color);
        testCity = new City(name, 0, 0, cityColor);
    }

    @Given("A research station on the city")
    public void a_research_station_on_the_city() {
        testCity.buildResearchStation();
    }

    @And("a {string} disease level of {int}")
    public void aDiseaseLevelOf(String color, int level) {
        for (int i = 0; i < level; i++) {
            diseaseCubeBank.infectCity(testCity, stringToEnum(color), outbreakManager);
        }
    }

    @When("{string} is treated")
    public void isTreated(String color) {
        testCity.treatDisease(stringToEnum(color), diseaseCubeBank);
    }

    @Then("the {string} disease level is {int}")
    public void theDiseaseLevelIs(String color, int level) {
        assertEquals(testCity.getInfectionLevel(stringToEnum(color)), level);
    }

    @When("an additional {string} cube is added")
    public void anAdditionalCubeIsAdded(String color) {
        diseaseCubeBank.infectCity(testCity, stringToEnum(color), outbreakManager);
    }

    @And("a second city {string} with color {string}")
    public void aSecondCityWithColor(String name, String color) {
//        Point cityLocation = new Point(0, 0);
        CityColor cityColor = stringToEnum(color);
        testCity2 = new City(name, 0, 0, cityColor);
    }

    @When("an attempt is made to infect the city")
    public void anAttemptIsMadeToInfectTheCity() {
        diseaseCubeBank.infectCity(testCity, CityColor.BLUE, outbreakManager);
    }

    @Then("the city will not be infected")
    public void theCityWillNotBeInfected() {
        assertEquals(0, testCity.getInfectionLevel(CityColor.BLUE));
    }

    @And("the cities are connected")
    public void theCitiesAreConnected() {
        testCity.connectedCities.add(testCity2);
        testCity2.connectedCities.add(testCity);
    }

    @When("an attempt is made to infect the second city")
    public void anAttemptIsMadeToInfectTheSecondCity() {
        diseaseCubeBank.infectCity(testCity2, CityColor.BLUE, outbreakManager);
    }

    @Then("the second city will not be infected")
    public void theSecondCityWillNotBeInfected() {
        diseaseCubeBank.infectCity(testCity2, CityColor.BLUE, outbreakManager);
    }
}

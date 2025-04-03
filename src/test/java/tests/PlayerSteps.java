package tests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import main.*;
import main.roles.QuarantineSpecialist;
import main.roles.Researcher;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerSteps {
    Player testPlayer;
    Player secondTestPlayer;
    PlayerCard testCard;
    ActionFailedException actionFailedException;
    DiseaseCubeBank diseaseCubeBank;

    @Given("A player on the city")
    public void a_player_on_the_city() {
        testPlayer = new Player(Color.RED, CitySteps.testCity);
        diseaseCubeBank = new DiseaseCubeBank();
    }

    @When("The player attempts to build a research station on the city")
    public void the_player_attempts_to_build_a_research_station_the_city() {
        try {
            testPlayer.buildResearchStation();
        } catch (ActionFailedException actionFailedException) {
            this.actionFailedException = actionFailedException;
        }

    }

    @Then("the player is notified that the action failed")
    public void the_player_is_notified_that_the_action_failed() {
        assertNotEquals(actionFailedException, null);
    }

    @When("The player attempts to drive to the second city")
    public void thePlayerAttemptsToDriveTo() {
        try {
            testPlayer.move(CitySteps.testCity);
        } catch (ActionFailedException actionFailedException) {
            this.actionFailedException = actionFailedException;
        }
    }

    @When("The player attempts to take a direct flight to the second city")
    public void thePlayerAttemptsToTakeADirectFlightTo() {
        try {
            testPlayer.directFlight(CitySteps.testCity2);
        } catch (ActionFailedException actionFailedException) {
            this.actionFailedException = actionFailedException;
        }
    }

    @When("The player attempts to charter a flight to the second city")
    public void thePlayerAttemptsToCharterAFlightTo() {
        try {
            testPlayer.charterFlight(CitySteps.testCity2);
        } catch (ActionFailedException actionFailedException) {
            this.actionFailedException = actionFailedException;
        }
    }

    @When("The player attempts to take a shuttle flight to the second city")
    public void thePlayerAttemptsToTakeAShuttleFlightTo() {
        try {
            testPlayer.shuttleFlight(CitySteps.testCity2);
        } catch (ActionFailedException actionFailedException) {
            this.actionFailedException = actionFailedException;
        }
    }

    @And("A player on the second city")
    public void aPlayerOnTheSecondCity() {
        secondTestPlayer = new Player(Color.BLUE, CitySteps.testCity2);
    }

    @When("the players attempt to share knowledge")
    public void thePlayersAttemptToShareKnowledge() {
        if(CitySteps.testCity == null) {
            throw new RuntimeException("Need a city to run this test!");
        }
        try {
            testPlayer.shareKnowledgeGive(secondTestPlayer, new PlayerCard(CitySteps.testCity));
        } catch (ActionFailedException actionFailedException) {
            this.actionFailedException = actionFailedException;
        }
    }

    @When("the player attempts to treat a disease")
    public void thePlayerAttemptsToTreatADisease() {
        try {
            testPlayer.treatDisease(CityColor.BLUE,diseaseCubeBank);
        } catch (ActionFailedException actionFailedException) {
            this.actionFailedException = actionFailedException;
        }
    }

    @And("A Researcher on the city")
    public void aResearcherOnTheCity() {
        testPlayer = new Researcher(CitySteps.testCity2);
    }

    @And("The player has a card for the second city")
    public void thePlayerHasACardForTheSecondCity() {
        testCard = new PlayerCard(CitySteps.testCity2);
        testPlayer.drawCard(testCard);
    }

    @And("A second player on the city")
    public void aSecondPlayerOnTheCity() {
        secondTestPlayer = new Player(Color.BLUE, CitySteps.testCity);
    }

    @When("The first player gives the card to the second")
    public void theFirstPlayerGivesTheCardToTheSecond() {
        testPlayer.shareKnowledgeGive(secondTestPlayer, testCard);
    }

    @Then("The second player has the card")
    public void theSecondPlayerHasTheCard() {
        assertTrue(secondTestPlayer.getCardNames().contains(CitySteps.testCity2.name));
    }

    @And("A quarantine specialist on the city")
    public void aQuarantineSpecialistOnTheCity() {
        testPlayer = new QuarantineSpecialist(CitySteps.testCity);
        CitySteps.testCity.addPawn(testPlayer);
    }
}

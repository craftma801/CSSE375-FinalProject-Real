package tests;

import main.*;

import javax.swing.*;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class DummyGameWindow implements GameWindowInterface {
    @Override
    public void showWindow() {

    }

    @Override
    public void displayNextPlayerInfo(String nextPlayerName, int remainingActions) {

    }

    @Override
    public String promptSelectOption(PromptWindowInputs inputs) {
        return "";
    }

    @Override
    public void updateTreatmentIndicator(CityColor color, String message) {

    }

    @Override
    public String promptCureCards(String[] possibleValues) {
        return "";
    }

    @Override
    public void showGameOverMessage(String message) {

    }

    @Override
    public void displayMessage(String title, String message, int type) {

    }

    @Override
    public Player promptSelectPlayer(PromptWindowInputs inputs) {
        return null;
    }

    @Override
    public InfectionCard promptInfectionCard(PromptWindowInputs inputs) {
        return null;
    }

    @Override
    public PlayerCard promptSelectPlayerCard(PromptWindowInputs inputs) {
        return null;
    }

    @Override
    public void displayInfectionCards(InfectionCard[] cardsToDisplay, String title) {

    }

    @Override
    public CityColor promptColorToCure(CityColor[] colors) {
        return null;
    }

    @Override
    public void repaintGameBoard() {

    }

    @Override
    public JFrame getWindowFrame() {
        return null;
    }

    @Override
    public void destroyCurrentInfectionCardsDialog() {

    }

    @Override
    public void displayPlayerCards(Player[] players, Player currentPlayer) {

    }

    @Override
    public void updateInfectionRate(int infectionRate) {

    }

    @Override
    public void updateOutbreaks(int outbreaks) {

    }

    @Override
    public void updateRemainingActions(int remainingActions) {

    }

    @Override
    public CompletableFuture<City> selectCity(HashSet<City> possibleLocations) {
        return null;
    }
}

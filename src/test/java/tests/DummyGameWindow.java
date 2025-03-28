package tests;

import main.*;

import javax.swing.*;

public class DummyGameWindow implements GameWindowInterface {
    @Override
    public void showWindow() {

    }

    @Override
    public void displayNextPlayerInfo(String nextPlayerName, int remainingActions) {

    }

    @Override
    public String promptSelectOption(String[] options, String title, String message) {
        return "4";
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
    public Player promptSelectPlayer(Player[] options, String title, String message) {
        return null;
    }

    @Override
    public InfectionCard promptInfectionCard(InfectionCard[] infectionCards, String title, String message) {
        return null;
    }

    @Override
    public PlayerCard promptSelectPlayerCard(PlayerCard[] playerCards, String title, String message) {
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
}

package main;

import javax.swing.*;

public interface GameWindowInterface {

    void showWindow();
    void displayNextPlayerInfo(String nextPlayerName, int remainingActions);
    String promptSelectOption(String[] options, String title, String message);
    void updateTreatmentIndicator(CityColor color, String message);
    String promptCureCards(String[] possibleValues);
    void showGameOverMessage(String message);
    void displayMessage(String title, String message, int type);
    Player promptSelectPlayer(Player[] options, String title, String message);
    InfectionCard promptInfectionCard(InfectionCard[] infectionCards, String title, String message);
    PlayerCard promptSelectPlayerCard(PlayerCard[] playerCards, String title, String message);
    void displayInfectionCards(InfectionCard[] cardsToDisplay, String title);
    CityColor promptColorToCure(CityColor[] colors);
    void repaintGameBoard();
    JFrame getWindowFrame();
    void destroyCurrentInfectionCardsDialog();
    void displayPlayerCards(Player[] players, Player currentPlayer);
    void updateInfectionRate(int infectionRate);
    void updateOutbreaks(int outbreaks);
    void updateRemainingActions(int remainingActions);
}

package main;

import javax.swing.*;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public interface GameWindowInterface {

    void showWindow();
    void displayNextPlayerInfo(String nextPlayerName, int remainingActions);
    String promptSelectOption(PromptWindowInputs inputs);
    void updateTreatmentIndicator(CityColor color, String message);
    String promptCureCards(String[] possibleValues);
    void showGameOverMessage(String message);
    void displayMessage(String title, String message, int type);
    Player promptSelectPlayer(PromptWindowInputs inputs);
    InfectionCard promptInfectionCard(PromptWindowInputs inputs);
    PlayerCard promptSelectPlayerCard(PromptWindowInputs inputs);
    void displayInfectionCards(InfectionCard[] cardsToDisplay, String title);
    CityColor promptColorToCure(CityColor[] colors);
    void repaintGameBoard();
    JFrame getWindowFrame();
    void destroyCurrentInfectionCardsDialog();
    void displayPlayerCards(Player[] players, Player currentPlayer);
    void updateInfectionRate(int infectionRate);
    void updateOutbreaks(int outbreaks);
    void updateRemainingActions(int remainingActions);

    CompletableFuture<City> selectCity(HashSet<City> options);
}

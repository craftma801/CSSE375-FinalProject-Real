package main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;

public class Player {
    private final Color color;
    protected City currentLocation;
    protected ArrayList<PlayerCard> cardsInHand;
    public String name;
    protected String displayName;
    protected String playerNum;
    private Image icon;
    private final ResourceBundle bundle;

    public Player(Color color, City startLocation) {
        if (Pandemic.bundle != null) {
            this.bundle = Pandemic.bundle;
        } else {
            Locale locale = new Locale("en", "US");
            this.bundle = ResourceBundle.getBundle("messages", locale);
        }
        this.color = color;

        this.currentLocation = startLocation;
        cardsInHand = new ArrayList<>();
        generateIcon();
    }

    private void generateIcon() {
        Image pawnImage = Toolkit.getDefaultToolkit().getImage("assets/pawn.png");
//        ImageFilter colorFilter = new PawnImageFilter(color);
        ImageFilter colorFilter = new RGBImageFilter() {
            @Override
            public int filterRGB(int x, int y, int rgb) {
                return (rgb > 0) ? 0x00000000 : color.getRGB();
            }
        };
        FilteredImageSource filteredSrc = new FilteredImageSource(pawnImage.getSource(), colorFilter);
        this.icon = Toolkit.getDefaultToolkit().createImage(filteredSrc);
    }

    public Color getColor() {
        return this.color;
    }

    public City getCity() {
        return currentLocation;
    }

    public ArrayList<PlayerCard> getCardsInHand() {
        return cardsInHand;
    }

    public void drawCard(PlayerCard card) {
        if (card.isEpidemic) {
            return;
        }
        this.cardsInHand.add(card);
    }

    public PlayerCard discardCardAtIndex(int index) {
        return cardsInHand.remove(index);
    }

    public void discardCardWithName(String name) {
        int indexToRemove = -1;
        for (int i = 0; i < this.cardsInHand.size(); i++) {
            if (this.cardsInHand.get(i).name.equals(name)) {
                indexToRemove = i;
            }
        }
        discardCardAtIndex(indexToRemove);
    }

    public void move(City destination) {
        if (this.getCity().connectedCities.contains(destination)) {
            currentLocation.players.remove(this);
            this.currentLocation = destination;
            destination.players.add(this);
            return;
        }
        String cannotMoveToCity = bundle.getString("cannotMoveToThatCity");
        throw new ActionFailedException(cannotMoveToCity);
    }

    public HashSet<City> driveFerryDestinations() {
        return new HashSet<>(currentLocation.connectedCities);
    }

    public void directFlight(City destination) {
        for (int i = 0; i < cardsInHand.size(); i++) {
            City toFlyTo = cardsInHand.get(i).city;
            if (destination.equals(toFlyTo)) {
                cardsInHand.remove(i);
                currentLocation.players.remove(this);
                this.currentLocation = destination;
                destination.players.add(this);
                return;
            }
        }
        String cardMissing = bundle.getString("youDoNotHaveTheCardForThatCity");
        throw new ActionFailedException(cardMissing);
    }

    public HashSet<City> directFlightDestinations() {
        HashSet<City> possibleLocations = new HashSet<>();
        for(PlayerCard card : cardsInHand) {
            possibleLocations.add(card.city);
        }
        return possibleLocations;
    }

    public void charterFlight(City destination) {
        for (int i = 0; i < cardsInHand.size(); i++) {
            City cityInHand = cardsInHand.get(i).city;
            if (this.currentLocation.equals(cityInHand)) {
                cardsInHand.remove(i);
                currentLocation.players.remove(this);
                this.currentLocation = destination;
                destination.players.add(this);
                return;
            }
        }
        String cardMissing = bundle.getString("youDoNotHaveTheCardForThatCity");
        throw new ActionFailedException(cardMissing);
    }

    public void shuttleFlight(City shuttleDestination) {
        if (shuttleDestination.hasResearchStation()) {
            currentLocation.players.remove(this);
            this.currentLocation = shuttleDestination;
            shuttleDestination.players.add(this);
            return;
        }
        throw new ActionFailedException("Destination must have Research Station to use Shuttle Flight!");
    }

    public void shareKnowledgeTake(Player takingFrom, PlayerCard cardToTake) {
        for (int i = 0; i < takingFrom.cardsInHand.size(); i++) {
            if (takingFrom.cardsInHand.get(i).equals(cardToTake) && cardMatchesCurrentLocation(cardToTake)) {
                takingFrom.discardCardAtIndex(i);
                this.cardsInHand.add(cardToTake);
                return;
            }

            String knowledgeShareFailed = bundle.getString("failedToTakeKnowledge");
            // left in temporarily until below is implemented
            throw new ActionFailedException(knowledgeShareFailed);

            // This is so action failed does not show up when canceling or not on the same space as takingFrom player
            // still needs working, and questions to ask.
//            else{
//                JOptionPane.showMessageDialog(null, "Cannot take knowledge from selected player");
//                return;
//            }
        }
    }

    public void shareKnowledgeGive(Player givingTo, PlayerCard cardToGive) {
        for (int i = 0; i < cardsInHand.size(); i++) {
            if (cardsInHand.get(i).equals(cardToGive) && (givingTo.cardMatchesCurrentLocation(cardToGive)
                    || cardMatchesCurrentLocation(cardToGive))) {
                this.discardCardAtIndex(i);
                givingTo.drawCard(cardToGive);
                return;
            }
        }
        String knowledgeShareFailed = bundle.getString("failedToShareKnowledge");
        throw new ActionFailedException(knowledgeShareFailed);
    }

    protected boolean cardMatchesCurrentLocation(PlayerCard card) {
        return card.city.equals(this.currentLocation);
    }

    public void buildResearchStation() {
        if (currentLocation.hasResearchStation()) {
            String stationAlreadyHere = bundle.getString("thereIsAlreadyAResearchStationHere");
            throw new ActionFailedException(stationAlreadyHere);
        }
        for (int i = 0; i < cardsInHand.size(); i++) {
            City targetCity = cardsInHand.get(i).city;
            if (targetCity.name.equals(currentLocation.name)) {
                cardsInHand.remove(i);
                currentLocation.buildResearchStation();
                return;
            }
        }
        String doNotHaveCard = bundle.getString("youDoNotHaveTheCardForThisCity");
        throw new ActionFailedException(doNotHaveCard);
    }

    public void treatDisease(CityColor colorToTreat, DiseaseCubeBank diseaseCubeBank) {
        boolean result = this.currentLocation.treatDisease(colorToTreat, diseaseCubeBank);
        if (!result) {
            String failedToTreat = bundle.getString("failedToTreatDisease");
            throw new ActionFailedException(failedToTreat);
        }
    }

    public int handSize() {
        return this.cardsInHand.size();
    }

    public ArrayList<String> getCardNames() {
        ArrayList<String> cardNames = new ArrayList<>();
        for (PlayerCard playerCard : cardsInHand) {
            cardNames.add(playerCard.getName());
        }
        return cardNames;
    }

    public void forceRelocatePlayer(City destination) {
        currentLocation.players.remove(this);
        this.currentLocation = destination;
        destination.players.add(this);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Image getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}

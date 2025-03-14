package main;

import java.util.Locale;
import java.util.ResourceBundle;

public class PlayerCard {
    City city;
    String name;
    CityColor color;
    boolean isEpidemic = false;

    public PlayerCard(City city) {
        this.city = city;
        this.name = city.name;
        this.color = city.defaultColor();
    }

    public PlayerCard(boolean isEpidemic) {
        ResourceBundle bundle = Pandemic.bundle;
        if (bundle == null) {
            Locale locale = new Locale("en", "US");
            bundle = ResourceBundle.getBundle("messages", locale);
        }
        this.name = bundle.getString("epidemic");
        this.isEpidemic = isEpidemic;
    }

    public PlayerCard(EventName eventName) {
        this.name = eventName.toString();
        this.isEpidemic = false;
        this.color = CityColor.EVENT_COLOR;
    }

    public boolean isEvent() {
        return this.color == CityColor.EVENT_COLOR;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

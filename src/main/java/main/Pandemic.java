package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class Pandemic {
    public static final int BOARD_WIDTH = 2200;
    public static final int BOARD_HEIGHT = 1400;
    public static final Dimension BOARD_SIZE = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private static BoardStatusController boardStatusController;
    public static ResourceBundle bundle;
    public static Locale locale;

    public static void main(String[] args) {
        locale = GameWindow.selectLocale("Select your Locale");
        bundle = ResourceBundle.getBundle("messages", locale);
        int numPlayers = Integer.parseInt(GameWindow.selectNumberOfPlayers());
        ArrayList<City> cityMap = createMap();
               boardStatusController = new BoardStatusController(new GameWindow(cityMap), cityMap, numPlayers);
        boardStatusController.setup();
        boardStatusController.startGame();
        boardStatusController.displayGame();
    }

    public static void handleAction(PlayerAction playerAction) {
        boardStatusController.handleAction(playerAction);
    }

    private static City createCity(String name, Point location, CityColor color) {
        return new City(name, location, color);
    }

    public static ArrayList<City> createMap() {
        ArrayList<City> cityMap = new ArrayList<>();
        City LA = createCity(bundle.getString("losAngeles"), new Point(135, 610), CityColor.YELLOW);
        cityMap.add(LA);
        City mexicoCity = createCity(bundle.getString("mexicoCity"), new Point(290, 660), CityColor.YELLOW);
        cityMap.add(mexicoCity);
        City miami = createCity(bundle.getString("miami"), new Point(485, 635), CityColor.YELLOW);
        cityMap.add(miami);
        City bogota = createCity(bundle.getString("bogota"), new Point(465, 785), CityColor.YELLOW);
        cityMap.add(bogota);
        City lima = createCity(bundle.getString("lima"), new Point(405, 945), CityColor.YELLOW);
        cityMap.add(lima);
        City santiago = createCity(bundle.getString("santiago"), new Point(430, 1105), CityColor.YELLOW);
        cityMap.add(santiago);
        City buenosAires = createCity(bundle.getString("buenosAires"), new Point(605, 1080), CityColor.YELLOW);
        cityMap.add(buenosAires);
        City saoPaulo = createCity(bundle.getString("saoPaulo"), new Point(700, 965), CityColor.YELLOW);
        cityMap.add(saoPaulo);
        City lagos = createCity(bundle.getString("lagos"), new Point(1015, 755), CityColor.YELLOW);
        cityMap.add(lagos);
        City kinshasa = createCity(bundle.getString("kinshasa"), new Point(1120, 860), CityColor.YELLOW);
        cityMap.add(kinshasa);
        City khartoum = createCity(bundle.getString("khartoum"), new Point(1225, 725), CityColor.YELLOW);
        cityMap.add(khartoum);
        City johannesburg = createCity(bundle.getString("johannesburg"), new Point(1210, 1005), CityColor.YELLOW);
        cityMap.add(johannesburg);
        City bangkok = createCity(bundle.getString("bangkok"), new Point(1740, 695), CityColor.RED);
        cityMap.add(bangkok);
        City jakarta = createCity(bundle.getString("jakarta"), new Point(1740, 895), CityColor.RED);
        cityMap.add(jakarta);
        City HCMC = createCity(bundle.getString("hoChiMinhCity"), new Point(1840, 800), CityColor.RED);
        cityMap.add(HCMC);
        City hongKong = createCity(bundle.getString("hongKong"), new Point(1840, 635), CityColor.RED);
        cityMap.add(hongKong);
        City shanghai = createCity(bundle.getString("shanghai"), new Point(1820, 510), CityColor.RED);
        cityMap.add(shanghai);
        City beijing = createCity(bundle.getString("beijing"), new Point(1810, 405), CityColor.RED);
        cityMap.add(beijing);
        City seoul = createCity(bundle.getString("seoul"), new Point(1955, 400), CityColor.RED);
        cityMap.add(seoul);
        City taipei = createCity(bundle.getString("taipei"), new Point(1965, 610), CityColor.RED);
        cityMap.add(taipei);
        City manila = createCity(bundle.getString("manila"), new Point(2000, 795), CityColor.RED);
        cityMap.add(manila);
        City sydney = createCity(bundle.getString("sydney"), new Point(2100, 1095), CityColor.RED);
        cityMap.add(sydney);
        City tokyo = createCity(bundle.getString("tokyo"), new Point(2075, 455), CityColor.RED);
        cityMap.add(tokyo);
        City osaka = createCity(bundle.getString("osaka"), new Point(2085, 566), CityColor.RED);
        cityMap.add(osaka);
        City SF = createCity(bundle.getString("sanFrancisco"), new Point(105, 460), CityColor.BLUE);
        cityMap.add(SF);
        City chicago = createCity(bundle.getString("chicago"), new Point(315, 405), CityColor.BLUE);
        cityMap.add(chicago);
        City atlanta = createCity(bundle.getString("atlanta"), new Point(380, 515), CityColor.BLUE);
        cityMap.add(atlanta);
        City montreal = createCity(bundle.getString("montreal"), new Point(485, 400), CityColor.BLUE);
        cityMap.add(montreal);
        City washington = createCity(bundle.getString("washington"), new Point(555, 510), CityColor.BLUE);
        cityMap.add(washington);
        City NY = createCity(bundle.getString("newYork"), new Point(610, 420), CityColor.BLUE);
        cityMap.add(NY);
        City madrid = createCity(bundle.getString("madrid"), new Point(890, 475), CityColor.BLUE);
        cityMap.add(madrid);
        City london = createCity(bundle.getString("london"), new Point(915, 330), CityColor.BLUE);
        cityMap.add(london);
        City paris = createCity(bundle.getString("paris"), new Point(1035, 405), CityColor.BLUE);
        cityMap.add(paris);
        City essen = createCity(bundle.getString("essen"), new Point(1075, 305), CityColor.BLUE);
        cityMap.add(essen);
        City milan = createCity(bundle.getString("milan"), new Point(1140, 375), CityColor.BLUE);
        cityMap.add(milan);
        City stPetersburg = createCity(bundle.getString("stPetersburg"), new Point(1255, 275), CityColor.BLUE);
        cityMap.add(stPetersburg);
        City algiers = createCity(bundle.getString("algiers"), new Point(1070, 555), CityColor.BLACK);
        cityMap.add(algiers);
        City istanbul = createCity(bundle.getString("istanbul"), new Point(1215, 460), CityColor.BLACK);
        cityMap.add(istanbul);
        City cairo = createCity(bundle.getString("cairo"), new Point(1190, 585), CityColor.BLACK);
        cityMap.add(cairo);
        City riyadh = createCity(bundle.getString("riyadh"), new Point(1345, 666), CityColor.BLACK);
        cityMap.add(riyadh);
        City baghdad = createCity(bundle.getString("baghdad"), new Point(1330, 535), CityColor.BLACK);
        cityMap.add(baghdad);
        City moscow = createCity(bundle.getString("moscow"), new Point(1340, 375), CityColor.BLACK);
        cityMap.add(moscow);
        City tehran = createCity(bundle.getString("tehran"), new Point(1450, 445), CityColor.BLACK);
        cityMap.add(tehran);
        City karachi = createCity(bundle.getString("karachi"), new Point(1480, 580), CityColor.BLACK);
        cityMap.add(karachi);
        City mumbai = createCity(bundle.getString("mumbai"), new Point(1500, 690), CityColor.BLACK);
        cityMap.add(mumbai);
        City delhi = createCity(bundle.getString("delhi"), new Point(1600, 540), CityColor.BLACK);
        cityMap.add(delhi);
        City chennai = createCity(bundle.getString("chennai"), new Point(1625, 765), CityColor.BLACK);
        cityMap.add(chennai);
        City kolkata = createCity(bundle.getString("kolkata"), new Point(1715, 575), CityColor.BLACK);
        cityMap.add(kolkata);

        LA.addConnection(sydney);
        LA.addConnection(SF);
        LA.addConnection(chicago);
        LA.addConnection(mexicoCity);

        mexicoCity.addConnection(LA);
        mexicoCity.addConnection(chicago);
        mexicoCity.addConnection(miami);
        mexicoCity.addConnection(bogota);
        mexicoCity.addConnection(lima);

        miami.addConnection(mexicoCity);
        miami.addConnection(atlanta);
        miami.addConnection(washington);
        miami.addConnection(bogota);

        bogota.addConnection(miami);
        bogota.addConnection(mexicoCity);
        bogota.addConnection(saoPaulo);
        bogota.addConnection(buenosAires);
        bogota.addConnection(lima);

        lima.addConnection(mexicoCity);
        lima.addConnection(bogota);
        lima.addConnection(santiago);

        santiago.addConnection(lima);

        buenosAires.addConnection(bogota);
        buenosAires.addConnection(saoPaulo);

        saoPaulo.addConnection(bogota);
        saoPaulo.addConnection(buenosAires);
        saoPaulo.addConnection(madrid);
        saoPaulo.addConnection(lagos);

        lagos.addConnection(saoPaulo);
        lagos.addConnection(khartoum);
        lagos.addConnection(kinshasa);

        kinshasa.addConnection(lagos);
        kinshasa.addConnection(khartoum);
        kinshasa.addConnection(johannesburg);

        khartoum.addConnection(johannesburg);
        khartoum.addConnection(kinshasa);
        khartoum.addConnection(lagos);
        khartoum.addConnection(cairo);

        johannesburg.addConnection(kinshasa);
        johannesburg.addConnection(khartoum);

        bangkok.addConnection(kolkata);
        bangkok.addConnection(chennai);
        bangkok.addConnection(jakarta);
        bangkok.addConnection(HCMC);
        bangkok.addConnection(hongKong);

        jakarta.addConnection(chennai);
        jakarta.addConnection(bangkok);
        jakarta.addConnection(HCMC);
        jakarta.addConnection(sydney);

        HCMC.addConnection(jakarta);
        HCMC.addConnection(bangkok);
        HCMC.addConnection(manila);
        HCMC.addConnection(hongKong);

        hongKong.addConnection(HCMC);
        hongKong.addConnection(bangkok);
        hongKong.addConnection(kolkata);
        hongKong.addConnection(shanghai);
        hongKong.addConnection(taipei);
        hongKong.addConnection(manila);

        shanghai.addConnection(hongKong);
        shanghai.addConnection(taipei);
        shanghai.addConnection(beijing);
        shanghai.addConnection(seoul);
        shanghai.addConnection(tokyo);

        beijing.addConnection(shanghai);
        beijing.addConnection(seoul);

        seoul.addConnection(tokyo);
        seoul.addConnection(beijing);
        seoul.addConnection(shanghai);

        taipei.addConnection(hongKong);
        taipei.addConnection(shanghai);
        taipei.addConnection(osaka);
        taipei.addConnection(madrid);

        manila.addConnection(taipei);
        manila.addConnection(hongKong);
        manila.addConnection(HCMC);
        manila.addConnection(SF);
        manila.addConnection(sydney);

        sydney.addConnection(jakarta);
        sydney.addConnection(manila);
        sydney.addConnection(LA);

        tokyo.addConnection(seoul);
        tokyo.addConnection(osaka);
        tokyo.addConnection(shanghai);
        tokyo.addConnection(SF);

        osaka.addConnection(tokyo);
        osaka.addConnection(taipei);

        SF.addConnection(tokyo);
        SF.addConnection(manila);
        SF.addConnection(LA);
        SF.addConnection(chicago);

        chicago.addConnection(SF);
        chicago.addConnection(LA);
        chicago.addConnection(mexicoCity);
        chicago.addConnection(atlanta);
        chicago.addConnection(montreal);

        atlanta.addConnection(chicago);
        atlanta.addConnection(washington);
        atlanta.addConnection(miami);

        montreal.addConnection(chicago);
        montreal.addConnection(washington);
        montreal.addConnection(NY);

        washington.addConnection(miami);
        washington.addConnection(atlanta);
        washington.addConnection(montreal);
        washington.addConnection(NY);

        NY.addConnection(montreal);
        NY.addConnection(washington);
        NY.addConnection(madrid);
        NY.addConnection(london);

        madrid.addConnection(saoPaulo);
        madrid.addConnection(algiers);
        madrid.addConnection(NY);
        madrid.addConnection(paris);
        madrid.addConnection(london);

        london.addConnection(NY);
        london.addConnection(madrid);
        london.addConnection(essen);
        london.addConnection(paris);

        paris.addConnection(madrid);
        paris.addConnection(london);
        paris.addConnection(algiers);
        paris.addConnection(milan);
        paris.addConnection(essen);

        essen.addConnection(london);
        essen.addConnection(paris);
        essen.addConnection(stPetersburg);
        essen.addConnection(milan);

        milan.addConnection(essen);
        milan.addConnection(paris);
        milan.addConnection(istanbul);

        stPetersburg.addConnection(essen);
        stPetersburg.addConnection(istanbul);
        stPetersburg.addConnection(moscow);

        algiers.addConnection(madrid);
        algiers.addConnection(paris);
        algiers.addConnection(istanbul);
        algiers.addConnection(cairo);

        istanbul.addConnection(algiers);
        istanbul.addConnection(milan);
        istanbul.addConnection(stPetersburg);
        istanbul.addConnection(moscow);
        istanbul.addConnection(cairo);
        istanbul.addConnection(baghdad);

        cairo.addConnection(algiers);
        cairo.addConnection(istanbul);
        cairo.addConnection(baghdad);
        cairo.addConnection(khartoum);
        cairo.addConnection(riyadh);

        riyadh.addConnection(cairo);
        riyadh.addConnection(baghdad);
        riyadh.addConnection(karachi);

        baghdad.addConnection(riyadh);
        baghdad.addConnection(cairo);
        baghdad.addConnection(istanbul);
        baghdad.addConnection(tehran);
        baghdad.addConnection(karachi);

        moscow.addConnection(istanbul);
        moscow.addConnection(stPetersburg);
        moscow.addConnection(tehran);

        tehran.addConnection(moscow);
        tehran.addConnection(baghdad);
        tehran.addConnection(karachi);
        tehran.addConnection(delhi);

        karachi.addConnection(tehran);
        karachi.addConnection(baghdad);
        karachi.addConnection(riyadh);
        karachi.addConnection(mumbai);
        karachi.addConnection(delhi);

        mumbai.addConnection(karachi);
        mumbai.addConnection(delhi);
        mumbai.addConnection(chennai);

        delhi.addConnection(tehran);
        delhi.addConnection(karachi);
        delhi.addConnection(mumbai);
        delhi.addConnection(chennai);
        delhi.addConnection(kolkata);

        chennai.addConnection(mumbai);
        chennai.addConnection(delhi);
        chennai.addConnection(kolkata);
        chennai.addConnection(bogota);
        chennai.addConnection(jakarta);

        kolkata.addConnection(delhi);
        kolkata.addConnection(bangkok);
        kolkata.addConnection(chennai);
        kolkata.addConnection(hongKong);

        return cityMap;
    }
}

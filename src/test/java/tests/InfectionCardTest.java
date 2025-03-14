package tests;

import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InfectionCardTest{

	private City testCity;
	private OutbreakManager outbreakManager;

	@BeforeEach
	public void setUp() {
		this.testCity = new City("Atlanta", new Point(200, 400), CityColor.BLUE);
		this.outbreakManager = new OutbreakManager(new DummyGameWindow());
	}

	@Test
	public void testInfect() {
		DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
		InfectionCard infectionCard = new InfectionCard(this.testCity);
		infectionCard.cardDrawn(DiseaseStatus.ACTIVE, diseaseCubeBank, outbreakManager);
		assertEquals(this.testCity.getInfectionLevel(CityColor.BLUE), 1);
	}

	@Test
	public void testSetupInfect() {
		DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
		City testCity2 = new City("Los Angeles", new Point(100, 500), CityColor.YELLOW);
		InfectionCard testCard1 = new InfectionCard(this.testCity);
		InfectionCard testCard2 = new InfectionCard(testCity2);
		testCard1.infectDuringSetup(3, diseaseCubeBank, outbreakManager);
		testCard2.infectDuringSetup(2, diseaseCubeBank, outbreakManager);
		assertEquals(this.testCity.getInfectionLevel(this.testCity.defaultColor()), 3);
		assertEquals(testCity2.getInfectionLevel(testCity2.defaultColor()), 2);
	}

	@Test
	public void testInfectDiscarded() {
		DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
		InfectionCard testCard = new InfectionCard(this.testCity);
		testCard.cardDrawn(DiseaseStatus.ACTIVE, diseaseCubeBank, outbreakManager);
		testCard.cardDrawn(DiseaseStatus.ACTIVE, diseaseCubeBank, outbreakManager);
		assertEquals(this.testCity.getInfectionLevel(this.testCity.defaultColor()), 1);
	}

	@Test
	public void testCardString() {
		InfectionCard card = new InfectionCard(testCity);
		assertEquals("Atlanta", card.toString());
	}
}
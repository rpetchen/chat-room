import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.udacity.java.nano.WebSocketChatApplication;
import edu.udacity.java.nano.chat.WebSocketChatServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
		WebSocketChatApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebSocketChatApplicationTest {

	private WebDriver driver;
	private String baseURL = "http://localhost:8080";
	private String userName = "rpetchen";
	private Map<Session, String> onlineSession;

	@Before
	public void setUp() {
		System.setProperty("webdriver.gecko.driver",
				"C:\\Users\\Ryan\\Documents\\chatroom-starter\\src\\test\\java\\resources\\geckodriver.exe");
		driver = new FirefoxDriver();
		driver.get(baseURL);
		WebElement username = driver.findElement(By.id("username"));

		username.sendKeys(userName);

		WebElement login = driver.findElement(By.xpath("/html/body/div/form/div[2]/a"));

		login.click();

		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
	}

	@Test
	public void testLogin() {

		WebElement welcomeName = driver.findElement(By.xpath("//*[@id=\"username\"]"));

		assertEquals(userName, welcomeName.getText());

	}

//
	@Test
	public void userJoin() {

		onlineSession = WebSocketChatServer.getOnlineSessions();
		int sessionSize = onlineSession.size();

		int userCount = Integer
				.parseInt(driver.findElement(By.xpath("/html/body/div[2]/div/div/div[2]/div[2]/span[3]")).getText());

		System.out.println(
				"This is the userCount from UI " + userCount + " And this is the session count " + sessionSize);

		assertEquals(sessionSize, userCount);

	}

	@Test
	public void chat() {
		String textToSend = "This is my test";
		WebElement chatBox = driver.findElement(By.xpath("//*[@id=\"msg\"]"));
		chatBox.sendKeys(textToSend);
		driver.findElement(By.xpath("/html/body/div[2]/div/div/div[1]/div[2]/div[2]/button[1]")).click();
		String chatBoxText = driver.findElement(By.xpath("/html/body/div[2]/div/div/div[2]/div[3]/div/div/div"))
				.getText();
		String text = chatBoxText.substring(9);
		assertEquals(text, textToSend);
	}

	@Test
	public void userLeave() {
		driver.findElement(By.xpath("/html/body/div[1]/div/a/i")).click();

		assertTrue(driver.findElement(By.id("username")).isDisplayed());
	}

}

import java.time.Duration;

import javax.crypto.Mac;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SmartBuyTestNgClass {
	public WebDriver driver;
	public int numOfTry = 6;

	// ---Note that the site doesn't have the stock balance, I tried the maximum
	// number and assume that, and it isn't fix
	public int maxOfStock = 5;

	String url = "https://smartbuy-me.com/smartbuystore/";
	SoftAssert softAssertProcess = new SoftAssert();

//------1. Test Login to SmartBuy WebSite--------------------------------------------- 
	@BeforeTest
	public void test_login() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(url);
		// -------Refresh Site to assure that there is nothing saved in browser cash
		driver.navigate().refresh();
		driver.findElement(By.xpath("/html/body/main/header/div[2]/div/div[2]/a")).click();

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50000));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,550)");
	}

//------2. Test Adding Items Process--------------------------------------------- 
	@Test(priority = 1)
	public void test_add_item_SAMSUNG_50_inch_to_cart() {
		driver.findElement(By.xpath("//*[@id=\"newtab-Featured\"]/div/div[1]/div/div/div/div[2]/div/div[1]/a")).click();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,250)");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10000));

// --------Adding the Items as the Number of Try -------------------------------------

		// ---check the number of try with max stock
		if (numOfTry <= maxOfStock) {
			for (int i = 0; i < numOfTry; i++) {
				driver.findElement(By.xpath("//*[@id=\"addToCartButton\"]")).click();
				driver.findElement(By.xpath("//*[@id=\"addToCartLayer\"]/a[2]")).click();
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10000));
			}

			// --------To Assert That The count in The Cart as The same of Number of
			// Try-------------------------------------

			String shoppCartCount = driver
					.findElement(By.xpath(
							"/html/body/main/header/div[4]/div/nav/div/div[3]/div/ul/li[1]/div/div/div[1]/a/div[1]"))
					.getText();
//			System.out.println("The Cart Shop Count String is : " + shoppCartCount);

			int shoppCartCountInt = Integer.parseInt(shoppCartCount);
			// System.out.println("The Cart Shop Count Integer is: " + shoppCartCountInt);

			softAssertProcess.assertEquals(numOfTry, shoppCartCountInt, "The Count Shop Cart is correct");
			// -----test wrong values to assure invalid test
//				softAssertProcess.assertEquals(3, shoppCartCountInt, "The Count Shop Cart is wrong");
			softAssertProcess.assertAll();
		} else {

			System.out.println("You choose above the max items in stock");

			for (int i = 0; i < maxOfStock; i++) {
				driver.findElement(By.xpath("//*[@id=\"addToCartButton\"]")).click();
				driver.findElement(By.xpath("//*[@id=\"addToCartLayer\"]/a[2]")).click();
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10000));
			}

			// --------To Assert That The count in The Cart as The max items in stock----

			String shoppCartCount = driver
					.findElement(By.xpath(
							"/html/body/main/header/div[4]/div/nav/div/div[3]/div/ul/li[1]/div/div/div[1]/a/div[1]"))
					.getText();
//System.out.println("The Cart Shop Count String is : " + shoppCartCount);

			int shoppCartCountInt = Integer.parseInt(shoppCartCount);
//System.out.println("The Cart Shop Count Integer is: " + shoppCartCountInt);

			softAssertProcess.assertEquals(maxOfStock, shoppCartCountInt, "The Max Count Shop Cart is correct");
//-----test wrong values to assure invalid test
//	softAssertProcess.assertEquals(3, shoppCartCountInt, "The Max Count Shop Cart is wrong");
			softAssertProcess.assertAll();

		}

		js.executeScript("window.scrollBy(0,-450)");
	}

//------3. Test The Correct Price after adding items--------------------------------------------- 
	@Test(priority = 2)
	public void test_check_the_correct_price() {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(100000));

		String itemPrice = driver
				.findElement(By.xpath(
						"/html/body/main/div[3]/div[1]/div[1]/div[3]/div[2]/div[2]/div[1]/div[1]/div[3]/span[2]"))
				.getText();
//		System.out.println("The Item Price is: " + itemPrice);

		String itemPriceNew = itemPrice.replace(" JOD", "").replaceAll(",", "").trim();
//		System.out.println("The Item Price New as number without any character or space: " + itemPriceNew);

		double itemPriceDouble = Double.parseDouble(itemPriceNew);
//	System.out.println("The Item Price Double is: " + itemPriceDouble);

//-----------to calculate the total price-------------------------------------

		Double chkTotalPrice;
		if (numOfTry <= maxOfStock) {
			chkTotalPrice = itemPriceDouble * numOfTry;
		} else {
			chkTotalPrice = itemPriceDouble * maxOfStock;
		}
//	System.out.println("The Total Price Calculated is: " + chkTotalPrice);

		// ----to click on shopping cart to get the total price
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(200000));
		driver.findElement(
				By.xpath("/html/body/main/header/div[4]/div/nav/div/div[3]/div/ul/li[1]/div/div/div[1]/a/div[1]"))
				.click();

		String totalPrice = driver.findElement(By.xpath("//*[@id=\"cboxLoadedContent\"]/div/div/div[2]/div[1]/h4[2]"))
				.getText();
//	System.out.println("The Total Price is: " + totalPrice);

		String totalPriceNew = totalPrice.replace(" JOD", "").replaceAll(",", "").trim();
//	System.out.println("The Total Price New as number without any character or space: " + totalPriceNew);

		Double totalPriceDouble = Double.parseDouble(totalPriceNew);
//	System.out.println("The Total Price Double: " + totalPriceDouble);

// --------To Assert That The total Price as the calculated price---------------------

		softAssertProcess.assertEquals(totalPriceDouble, chkTotalPrice, "The Total Price is correct");

		// -----test wrong values to assure invalid test
//		softAssertProcess.assertEquals(totalPriceDouble, 2000.0, "The Total Price is wrong");

		softAssertProcess.assertAll();

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(100000));
	}

//------4. Test Close SmartBuy WebSite--------------------------------------------- 
	@AfterTest()
	public void test_close_website() throws InterruptedException {
		Thread.sleep(5000);
		driver.quit();
	}

}
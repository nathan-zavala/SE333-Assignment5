package org.example.playwrightTraditional;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;
import com.microsoft.playwright.options.AriaRole;

public class BookstoreTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    @DisplayName("TestCase Bookstore")
    void testBookstore() {
        page.navigate("https://depaul.bncollege.com/");

        // Search for earbuds
        page.locator("#bned_site_search").fill("earbuds");
        page.locator("#bned_site_search").press("Enter");

        // Filter by Brand: JBL
        page.locator("[data-target='#facet-brand']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='JBL'])").first().click();
        
        // Filter by Color: Black
        page.waitForLoadState();
        page.locator("[data-target='#facet-Color']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Black'])").first().click();

        // Filter by Price: Over $50
        page.waitForLoadState();
        page.locator("[data-target='#facet-price']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Over $50'])").first().click();
        
        // Wait for page to load after price filter then click product
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming").first().click();

        // Wait for product page to load
        page.waitForLoadState();
        page.waitForTimeout(1000);

        // Assert product details
        assertThat(page.locator("h1.name").first()).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("668972707");
        assertThat(page.locator("body")).containsText("$164.98");
        assertThat(page.locator("body")).containsText("Adaptive noise cancelling");

        // Assert product details
        assertThat(page.locator("h1.name").first()).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("$164.98");
        assertThat(page.locator("body")).containsText("Adaptive noise cancelling");

        // Add to cart
        page.getByText("Add to Cart").click();

        // Wait for cart to update and assert
        page.waitForTimeout(3000);
        assertThat(page.locator("body")).containsText("Item added to cart");
        assertThat(page.locator("body")).containsText("Cart 1 items");

        // Click cart icon - use first() since there are two cart links (desktop + mobile)
        page.locator("a.js-mini-cart-link").first().click();
    }

    @Test
    @DisplayName("TestCase Your Shopping Cart Page")
    void testShoppingCart() {
        // First add item to cart (same as testBookstore)
        page.navigate("https://depaul.bncollege.com/");
        page.locator("#bned_site_search").fill("earbuds");
        page.locator("#bned_site_search").press("Enter");
        page.locator("[data-target='#facet-brand']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='JBL'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-Color']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Black'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-price']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Over $50'])").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("Add to Cart").click();
        page.waitForTimeout(3000);

        // Now navigate to cart
        page.locator("a.js-mini-cart-link").first().click();
        page.waitForLoadState();

        // Assert we are on the shopping cart page
        assertThat(page.locator("body")).containsText("Your Shopping Cart");

        // Assert product name and price
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("$164.98");

        // Select FAST In-Store Pickup
        page.getByText("FAST In-Store Pickup").click();
        page.waitForTimeout(2000);

        // Assert sidebar totals
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        // Enter promo code TEST and assert rejection
        page.waitForTimeout(2000);
        page.locator("input[name='voucherCode']").fill("TEST");
        page.locator("#js-voucher-apply-btn").click();
        page.waitForTimeout(2000);
        assertThat(page.locator("body")).containsText("The coupon code entered is not valid.");

        // Proceed to checkout
        page.locator("[aria-label='Proceed To Checkout']").first().click();
        page.waitForLoadState();
    }

    @Test
    @DisplayName("TestCase Create Account Page")
    void testCreateAccount() {
        // Add item to cart and navigate to checkout
        page.navigate("https://depaul.bncollege.com/");
        page.locator("#bned_site_search").fill("earbuds");
        page.locator("#bned_site_search").press("Enter");
        page.locator("[data-target='#facet-brand']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='JBL'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-Color']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Black'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-price']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Over $50'])").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("Add to Cart").click();
        page.waitForTimeout(3000);
        page.locator("a.js-mini-cart-link").first().click();
        page.waitForLoadState();
        page.getByText("FAST In-Store Pickup").click();
        page.waitForTimeout(2000);
        page.locator("input[name='voucherCode']").fill("TEST");
        page.locator("#js-voucher-apply-btn").click();
        page.waitForTimeout(2000);
        page.locator("[aria-label='Proceed To Checkout']").first().click();
        page.waitForLoadState();

        // Assert Create Account label is present
        assertThat(page.locator("body")).containsText("Create Account");

        // Click Proceed as Guest
        page.getByText("Proceed as Guest").click();
        page.waitForLoadState();
    }

    @Test
    @DisplayName("TestCase Contact Information Page")
    void testContactInformation() {
        // Add item to cart and navigate to checkout
        page.navigate("https://depaul.bncollege.com/");
        page.locator("#bned_site_search").fill("earbuds");
        page.locator("#bned_site_search").press("Enter");
        page.locator("[data-target='#facet-brand']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='JBL'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-Color']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Black'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-price']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Over $50'])").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("Add to Cart").click();
        page.waitForTimeout(3000);
        page.locator("a.js-mini-cart-link").first().click();
        page.waitForLoadState();
        page.getByText("FAST In-Store Pickup").click();
        page.waitForTimeout(2000);
        page.locator("input[name='voucherCode']").fill("TEST");
        page.locator("#js-voucher-apply-btn").click();
        page.waitForTimeout(2000);
        page.locator("[aria-label='Proceed To Checkout']").first().click();
        page.waitForLoadState();
        page.getByText("Proceed as Guest").click();
        page.waitForLoadState();

        // Assert we are on Contact Information page
        assertThat(page.locator("body")).containsText("Contact Information");

        // Enter contact details
        page.locator("input[name='firstName']").fill("John");
        page.locator("input[name='lastName']").fill("Doe");
        page.locator("input[name='emailAddress']").fill("johndoe@test.com");
        page.locator("input[name='phoneNumberHidden']").fill("3125551234");

        // Assert sidebar totals
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        // Click Continue
        page.getByText("Continue").first().click();
        page.waitForLoadState();
    }

    @Test
    @DisplayName("TestCase Pickup Information")
    void testPickupInformation() {
        // Add item to cart and navigate to pickup information page
        page.navigate("https://depaul.bncollege.com/");
        page.locator("#bned_site_search").fill("earbuds");
        page.locator("#bned_site_search").press("Enter");
        page.locator("[data-target='#facet-brand']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='JBL'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-Color']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Black'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-price']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Over $50'])").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("Add to Cart").click();
        page.waitForTimeout(3000);
        page.locator("a.js-mini-cart-link").first().click();
        page.waitForLoadState();
        page.getByText("FAST In-Store Pickup").click();
        page.waitForTimeout(2000);
        page.locator("input[name='voucherCode']").fill("TEST");
        page.locator("#js-voucher-apply-btn").click();
        page.waitForTimeout(2000);
        page.locator("[aria-label='Proceed To Checkout']").first().click();
        page.waitForLoadState();
        page.getByText("Proceed as Guest").click();
        page.waitForLoadState();
        page.locator("input[name='firstName']").fill("John");
        page.locator("input[name='lastName']").fill("Doe");
        page.locator("input[name='emailAddress']").fill("johndoe@test.com");
        page.locator("input[name='phoneNumberHidden']").fill("3125551234");
        page.getByText("Continue").first().click();
        page.waitForLoadState();
        page.waitForTimeout(2000);

        // Assert contact information
        assertThat(page.locator("body")).containsText("John");
        assertThat(page.locator("body")).containsText("Doe");
        assertThat(page.locator("body")).containsText("johndoe@test.com");
        assertThat(page.locator("body")).containsText("3125551234");

        // Assert pickup location
        assertThat(page.locator("body")).containsText("DePaul University Loop Campus & SAIC");

        // Assert pickup person
        assertThat(page.locator("body")).containsText("I'll pick them up");

        // Assert sidebar totals
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        // Assert pickup item and price
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("164.98");

        // Click Continue
        // Click Continue
        page.locator("button.btn-primary").last().click();
        page.waitForLoadState();
    }

    @Test
    @DisplayName("TestCase Payment Information")
    void testPaymentInformation() {
        // Navigate all the way to payment information page
        page.navigate("https://depaul.bncollege.com/");
        page.locator("#bned_site_search").fill("earbuds");
        page.locator("#bned_site_search").press("Enter");
        page.locator("[data-target='#facet-brand']").click();
        page.waitForTimeout(2000);
        page.locator("label:has(input[alt='JBL'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-Color']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Black'])").first().click();
        page.waitForLoadState();
        page.locator("[data-target='#facet-price']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Over $50'])").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming").first().click();
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("Add to Cart").click();
        page.waitForTimeout(3000);
        page.locator("a.js-mini-cart-link").first().click();
        page.waitForLoadState();
        page.getByText("FAST In-Store Pickup").click();
        page.waitForTimeout(2000);
        page.locator("input[name='voucherCode']").fill("TEST");
        page.locator("#js-voucher-apply-btn").click();
        page.waitForTimeout(2000);
        page.locator("[aria-label='Proceed To Checkout']").first().click();
        page.waitForLoadState();
        page.getByText("Proceed as Guest").click();
        page.waitForLoadState();
        page.locator("input[name='firstName']").fill("John");
        page.locator("input[name='lastName']").fill("Doe");
        page.locator("input[name='emailAddress']").fill("johndoe@test.com");
        page.locator("input[name='phoneNumberHidden']").fill("3125551234");
        page.getByText("Continue").first().click();
        page.waitForLoadState();
        page.waitForTimeout(2000);
        page.locator("button.btn-primary").last().click();
        page.waitForLoadState();
        page.waitForTimeout(2000);

        // Assert sidebar totals (tax is now calculated)
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("17.22");
        assertThat(page.locator("body")).containsText("185.20");

        // Assert pickup item and price
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("164.98");

        // Click Back to Cart
        page.locator("a[href='/cart']").first().click();
        page.waitForLoadState();
    }
}
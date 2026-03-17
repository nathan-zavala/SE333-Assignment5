package org.example.playwrightLLM;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;

public class BookstoreLLMTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void tearDownAll() {
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContext() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    @DisplayName("Search earbuds and verify cart item count")
    void testAddJblEarbudsToCart() {
        page.navigate("https://depaul.bncollege.com/");

        // Search for earbuds
        page.locator("#bned_site_search").fill("earbuds");
        page.locator("#bned_site_search").press("Enter");

        // Filter by JBL brand
        page.locator("[data-target='#facet-brand']").click();
        page.waitForTimeout(2000);
        page.locator("label:has(input[alt='JBL'])").first().click();

        // Filter by Black color
        page.waitForLoadState();
        page.locator("[data-target='#facet-Color']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Black'])").first().click();

        // Filter by price over $50
        page.waitForLoadState();
        page.locator("[data-target='#facet-price']").click();
        page.waitForTimeout(1000);
        page.locator("label:has(input[alt='Over $50'])").first().click();

        // Select JBL Quantum True Wireless earbuds
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming").first().click();

        // Add to cart
        page.waitForLoadState();
        page.waitForTimeout(1000);
        page.getByText("Add to Cart").click();

        // Wait for cart update and verify 1 item in cart
        page.waitForTimeout(3000);
        assertThat(page.locator("body")).containsText("Cart 1 items");

        // Open mini cart and verify 1 item appears
        page.locator("a.js-mini-cart-link").first().click();
        page.waitForLoadState();
        assertThat(page.locator("body")).containsText("Quantity");
        assertThat(page.locator("body")).containsText("1");
    }
}

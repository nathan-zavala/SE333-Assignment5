package org.example;

import org.example.Amazon.Amazon;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.RegularCost;
import org.example.Amazon.Database;
import org.example.Amazon.Item;
import org.example.Amazon.ShoppingCartAdaptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AmazonIntegrationTest {

    private Database database;
    private ShoppingCartAdaptor cart;

    @BeforeEach
    void resetDb() {
        if (database == null) {
            database = new Database();
        }
        database.resetDatabase();
        cart = new ShoppingCartAdaptor(database);
    }

    @AfterAll
    void closeDb() {
        if (database != null) {
            database.close();
        }
    }

    // ─────────────────────────────────────────────
    // Specification-based tests
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("specification-based")
    void emptyCartReturnsZeroTotal() {
        List<PriceRule> rules = Arrays.asList(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics());
        Amazon amazon = new Amazon(cart, rules);

        assertThat(amazon.calculate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based")
    void singleNonElectronicItemCalculatesCorrectly() {
        List<PriceRule> rules = Arrays.asList(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics());
        Amazon amazon = new Amazon(cart, rules);

        amazon.addToCart(new Item(ItemType.OTHER, "Book", 2, 10.0));

        // RegularCost: 2 * 10 = 20, DeliveryPrice: 1 item = 5, Electronics extra: 0
        assertThat(amazon.calculate()).isEqualTo(25.0);
    }

    @Test
    @DisplayName("specification-based")
    void singleElectronicItemIncludesExtraCost() {
        List<PriceRule> rules = Arrays.asList(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics());
        Amazon amazon = new Amazon(cart, rules);

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Laptop", 1, 500.0));

        // RegularCost: 500, DeliveryPrice: 5, Electronics extra: 7.5
        assertThat(amazon.calculate()).isEqualTo(512.5);
    }

    @Test
    @DisplayName("specification-based")
    void mixedCartAppliesAllRulesCorrectly() {
        List<PriceRule> rules = Arrays.asList(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics());
        Amazon amazon = new Amazon(cart, rules);

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Phone", 1, 200.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Book", 1, 20.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Pen", 1, 5.0));

        // RegularCost: 225, DeliveryPrice: 3 items = 5, Electronics extra: 7.5
        assertThat(amazon.calculate()).isEqualTo(237.5);
    }

    @Test
    @DisplayName("specification-based")
    void fourToTenItemsApplyMediumDeliveryFee() {
        List<PriceRule> rules = Arrays.asList(new RegularCost(), new DeliveryPrice());
        Amazon amazon = new Amazon(cart, rules);

        for (int i = 0; i < 4; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 10.0));
        }

        // RegularCost: 40, DeliveryPrice: 4 items = 12.5
        assertThat(amazon.calculate()).isEqualTo(52.5);
    }

    @Test
    @DisplayName("specification-based")
    void moreThanTenItemsApplyMaxDeliveryFee() {
        List<PriceRule> rules = Arrays.asList(new RegularCost(), new DeliveryPrice());
        Amazon amazon = new Amazon(cart, rules);

        for (int i = 0; i < 11; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 5.0));
        }

        // RegularCost: 55, DeliveryPrice: 11 items = 20
        assertThat(amazon.calculate()).isEqualTo(75.0);
    }

    @Test
    @DisplayName("specification-based")
    void cartPersistsItemsToDatabase() {
        amazon_addItem_thenCartReflectsItem();
    }

    private void amazon_addItem_thenCartReflectsItem() {
        List<PriceRule> rules = Arrays.asList(new RegularCost());
        Amazon amazon = new Amazon(cart, rules);

        Item item = new Item(ItemType.OTHER, "Notebook", 3, 7.5);
        amazon.addToCart(item);

        List<org.example.Amazon.Item> items = cart.getItems();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Notebook");
        assertThat(items.get(0).getQuantity()).isEqualTo(3);
        assertThat(items.get(0).getPricePerUnit()).isEqualTo(7.5);
    }

    // ─────────────────────────────────────────────
    // Structural-based tests
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("structural-based")
    void databaseResetsCorrectlyBetweenTests() {
        List<PriceRule> rules = Arrays.asList(new RegularCost());
        Amazon amazon = new Amazon(cart, rules);

        amazon.addToCart(new Item(ItemType.OTHER, "Widget", 1, 10.0));
        assertThat(cart.getItems()).hasSize(1);

        database.resetDatabase();
        ShoppingCartAdaptor freshCart = new ShoppingCartAdaptor(database);
        assertThat(freshCart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("structural-based")
    void multipleRulesAggregateIndependently() {
        DeliveryPrice delivery = new DeliveryPrice();
        ExtraCostForElectronics electronics = new ExtraCostForElectronics();
        RegularCost regular = new RegularCost();

        Amazon amazon = new Amazon(cart, Arrays.asList(regular, delivery, electronics));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Camera", 2, 150.0));

        double expected = (2 * 150.0) + 5.0 + 7.5; // regular + delivery(1 item) + electronics
        assertThat(amazon.calculate()).isEqualTo(expected);
    }

    @Test
    @DisplayName("structural-based")
    void onlyRegularCostRuleReturnsItemPriceOnly() {
        Amazon amazon = new Amazon(cart, Arrays.asList(new RegularCost()));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "TV", 1, 999.99));

        assertThat(amazon.calculate()).isEqualTo(999.99);
    }

    @Test
    @DisplayName("structural-based")
    void onlyDeliveryRuleIgnoresItemPrices() {
        Amazon amazon = new Amazon(cart, Arrays.asList(new DeliveryPrice()));
        amazon.addToCart(new Item(ItemType.OTHER, "Shoe", 1, 50.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Shirt", 1, 30.0));

        // 2 items => delivery = 5, item prices ignored
        assertThat(amazon.calculate()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("structural-based")
    void getItemsReturnsAllPersistedItems() {
        Amazon amazon = new Amazon(cart, Arrays.asList(new RegularCost()));
        amazon.addToCart(new Item(ItemType.OTHER, "Apple", 5, 1.0));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Tablet", 1, 300.0));

        List<Item> items = cart.getItems();
        assertThat(items).hasSize(2);
        assertThat(items.stream().map(Item::getName))
                .containsExactlyInAnyOrder("Apple", "Tablet");
    }

    @Test
    @DisplayName("structural-based")
    void electronicExtraCostOnlyAppliesOnceRegardlessOfQuantity() {
        Amazon amazon = new Amazon(cart, Arrays.asList(new ExtraCostForElectronics()));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Phone", 5, 100.0));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Tablet", 3, 200.0));

        // Even with multiple electronics, extra cost is applied once = 7.5
        assertThat(amazon.calculate()).isEqualTo(7.5);
    }
}
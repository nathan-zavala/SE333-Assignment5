package org.example;

import org.example.Amazon.Amazon;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.RegularCost;
import org.example.Amazon.Item;
import org.example.Amazon.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    private ShoppingCart mockCart;

    @BeforeEach
    void setUp() {
        mockCart = Mockito.mock(ShoppingCart.class);
    }

    // ─────────────────────────────────────────────
    // Specification-based tests
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("specification-based")
    void calculateReturnsZeroForEmptyCartWithAllRules() {
        when(mockCart.getItems()).thenReturn(Collections.emptyList());
        List<PriceRule> rules = Arrays.asList(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics());
        Amazon amazon = new Amazon(mockCart, rules);

        assertThat(amazon.calculate()).isEqualTo(0.0);
        verify(mockCart, times(3)).getItems();
    }

    @Test
    @DisplayName("specification-based")
    void regularCostCalculatesQuantityTimesPrice() {
        Item item = new Item(ItemType.OTHER, "Chair", 3, 20.0);
        when(mockCart.getItems()).thenReturn(List.of(item));

        Amazon amazon = new Amazon(mockCart, List.of(new RegularCost()));
        assertThat(amazon.calculate()).isEqualTo(60.0);
    }

    @Test
    @DisplayName("specification-based")
    void deliveryPriceIsZeroForEmptyCart() {
        when(mockCart.getItems()).thenReturn(Collections.emptyList());
        Amazon amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));

        assertThat(amazon.calculate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based")
    void deliveryPriceIsFiveForOneToThreeItems() {
        when(mockCart.getItems()).thenReturn(List.of(
                new Item(ItemType.OTHER, "Mug", 1, 5.0),
                new Item(ItemType.OTHER, "Plate", 1, 8.0)
        ));
        Amazon amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));
        assertThat(amazon.calculate()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("specification-based")
    void deliveryPriceIsTwelvePointFiveForFourToTenItems() {
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 1, 1.0),
                new Item(ItemType.OTHER, "B", 1, 1.0),
                new Item(ItemType.OTHER, "C", 1, 1.0),
                new Item(ItemType.OTHER, "D", 1, 1.0)
        );
        when(mockCart.getItems()).thenReturn(items);
        Amazon amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));

        assertThat(amazon.calculate()).isEqualTo(12.5);
    }

    @Test
    @DisplayName("specification-based")
    void deliveryPriceIsTwentyForMoreThanTenItems() {
        List<Item> items = Collections.nCopies(11, new Item(ItemType.OTHER, "X", 1, 1.0));
        when(mockCart.getItems()).thenReturn(items);
        Amazon amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));

        assertThat(amazon.calculate()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("specification-based")
    void electronicExtraCostAppliedWhenCartHasElectronic() {
        when(mockCart.getItems()).thenReturn(List.of(
                new Item(ItemType.ELECTRONIC, "Headphones", 1, 50.0)
        ));
        Amazon amazon = new Amazon(mockCart, List.of(new ExtraCostForElectronics()));
        assertThat(amazon.calculate()).isEqualTo(7.5);
    }

    @Test
    @DisplayName("specification-based")
    void electronicExtraCostNotAppliedWhenNoElectronics() {
        when(mockCart.getItems()).thenReturn(List.of(
                new Item(ItemType.OTHER, "Towel", 2, 12.0)
        ));
        Amazon amazon = new Amazon(mockCart, List.of(new ExtraCostForElectronics()));
        assertThat(amazon.calculate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based")
    void addToCartDelegatesToShoppingCart() {
        List<PriceRule> rules = List.of(new RegularCost());
        Amazon amazon = new Amazon(mockCart, rules);

        Item item = new Item(ItemType.OTHER, "Pen", 1, 2.0);
        amazon.addToCart(item);

        verify(mockCart, times(1)).add(item);
    }

    // ─────────────────────────────────────────────
    // Structural-based tests
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("structural-based")
    void calculateIteratesOverAllPriceRules() {
        PriceRule rule1 = mock(PriceRule.class);
        PriceRule rule2 = mock(PriceRule.class);
        when(mockCart.getItems()).thenReturn(Collections.emptyList());
        when(rule1.priceToAggregate(anyList())).thenReturn(10.0);
        when(rule2.priceToAggregate(anyList())).thenReturn(5.0);

        Amazon amazon = new Amazon(mockCart, Arrays.asList(rule1, rule2));
        double result = amazon.calculate();

        assertThat(result).isEqualTo(15.0);
        verify(rule1).priceToAggregate(anyList());
        verify(rule2).priceToAggregate(anyList());
    }

    @Test
    @DisplayName("structural-based")
    void calculateSumsAllRuleResults() {
        PriceRule rule1 = mock(PriceRule.class);
        PriceRule rule2 = mock(PriceRule.class);
        PriceRule rule3 = mock(PriceRule.class);
        when(mockCart.getItems()).thenReturn(Collections.emptyList());
        when(rule1.priceToAggregate(anyList())).thenReturn(100.0);
        when(rule2.priceToAggregate(anyList())).thenReturn(20.0);
        when(rule3.priceToAggregate(anyList())).thenReturn(3.5);

        Amazon amazon = new Amazon(mockCart, Arrays.asList(rule1, rule2, rule3));
        assertThat(amazon.calculate()).isEqualTo(123.5);
    }

    @Test
    @DisplayName("structural-based")
    void calculateWithNoRulesReturnsZero() {
        when(mockCart.getItems()).thenReturn(Collections.emptyList());
        Amazon amazon = new Amazon(mockCart, Collections.emptyList());

        assertThat(amazon.calculate()).isEqualTo(0.0);
        verify(mockCart, never()).getItems();
    }

    @Test
    @DisplayName("structural-based")
    void regularCostHandlesMultipleItemsWithDifferentQuantities() {
        List<Item> items = Arrays.asList(
                new Item(ItemType.OTHER, "A", 2, 5.0),  // 10
                new Item(ItemType.ELECTRONIC, "B", 3, 10.0) // 30
        );
        when(mockCart.getItems()).thenReturn(items);
        Amazon amazon = new Amazon(mockCart, List.of(new RegularCost()));

        assertThat(amazon.calculate()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("structural-based")
    void electronicExtraCostIsExactlySevenFifty() {
        when(mockCart.getItems()).thenReturn(List.of(
                new Item(ItemType.ELECTRONIC, "Router", 10, 0.0)
        ));
        Amazon amazon = new Amazon(mockCart, List.of(new ExtraCostForElectronics()));
        assertThat(amazon.calculate()).isEqualTo(7.50);
    }

    @Test
    @DisplayName("structural-based")
    void deliveryPriceBoundaryAtExactlyThreeItems() {
        List<Item> threeItems = Arrays.asList(
                new Item(ItemType.OTHER, "X", 1, 1.0),
                new Item(ItemType.OTHER, "Y", 1, 1.0),
                new Item(ItemType.OTHER, "Z", 1, 1.0)
        );
        when(mockCart.getItems()).thenReturn(threeItems);
        Amazon amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));

        assertThat(amazon.calculate()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("structural-based")
    void deliveryPriceBoundaryAtExactlyTenItems() {
        List<Item> tenItems = Collections.nCopies(10, new Item(ItemType.OTHER, "X", 1, 1.0));
        when(mockCart.getItems()).thenReturn(tenItems);
        Amazon amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));

        assertThat(amazon.calculate()).isEqualTo(12.5);
    }
}
using System.Collections.Generic;

namespace SupermarketReceipt
{
    public class ShoppingCart
    {
        private readonly List<ProductQuantity> _items = new List<ProductQuantity>();
        private readonly Dictionary<Product, double> _productQuantities = new Dictionary<Product, double>();


        public List<ProductQuantity> GetItems()
        {
            return new List<ProductQuantity>(_items);
        }

        public void AddItem(Product product)
        {
            AddItemQuantity(product, 1.0);
        }


        public void AddItemQuantity(Product product, double quantity)
        {
            _items.Add(new ProductQuantity(product, quantity));
            if (_productQuantities.ContainsKey(product))
            {
                var newAmount = _productQuantities[product] + quantity;
                _productQuantities[product] = newAmount;
            }
            else
            {
                _productQuantities.Add(product, quantity);
            }
        }

        public void HandleOffers(Receipt receipt, Dictionary<Product, Offer> offers, SupermarketCatalog catalog)
        {
            foreach (var p in _productQuantities.Keys)
            {
                var quantity = _productQuantities[p];
                if (offers.ContainsKey(p))
                {
                    var offer = offers[p];
                    var unitPrice = catalog.GetUnitPrice(p);
                    Discount discount = null;

                    switch (offer.OfferType)
                    {
                        case SpecialOfferType.ThreeForTwo:
                            discount = CalculateThreeForTwoDiscount(p, quantity, unitPrice);
                            break;
                        case SpecialOfferType.TwoForAmount:
                            discount = CalculateTwoForAmountDiscount(p, quantity, unitPrice, offer.Argument);
                            break;
                        case SpecialOfferType.TenPercentDiscount:
                            discount = CalculateTenPercentDiscount(p, quantity, unitPrice, offer.Argument);
                            break;
                        case SpecialOfferType.FiveForAmount:
                            discount = CalculateFiveForAmountDiscount(p, quantity, unitPrice, offer.Argument);
                            break;
                    }

                    if (discount != null)
                        receipt.AddDiscount(discount);
                }
            }
        }

        private Discount CalculateThreeForTwoDiscount(Product product, double quantity, double unitPrice)
        {
            var quantityAsInt = (int) quantity;
            if (quantityAsInt <= 2)
            {
                return null;
            }
            
            var numberOfThrees = quantityAsInt / 3;
            var discountAmount = quantity * unitPrice - (numberOfThrees * 2 * unitPrice + quantityAsInt % 3 * unitPrice);
            return new Discount(product, "3 for 2", -discountAmount);
        }

        private Discount CalculateTwoForAmountDiscount(Product product, double quantity, double unitPrice, double amount)
        {
            var quantityAsInt = (int) quantity;
            if (quantityAsInt < 2)
            {
                return null;
            }
            
            var total = amount * (quantityAsInt / 2) + quantityAsInt % 2 * unitPrice;
            var discountN = unitPrice * quantity - total;
            return new Discount(product, "2 for " + amount, -discountN);
        }

        private Discount CalculateTenPercentDiscount(Product product, double quantity, double unitPrice, double percentage)
        {
            return new Discount(product, percentage + "% off", -quantity * unitPrice * percentage / 100.0);
        }

        private Discount CalculateFiveForAmountDiscount(Product product, double quantity, double unitPrice, double amount)
        {
            var quantityAsInt = (int) quantity;
            if (quantityAsInt < 5)
            {
                return null;
            }
            
            var numberOfFives = quantityAsInt / 5;
            var discountTotal = unitPrice * quantity - (amount * numberOfFives + quantityAsInt % 5 * unitPrice);
            return new Discount(product, "5 for " + amount, -discountTotal);
        }
    }
}
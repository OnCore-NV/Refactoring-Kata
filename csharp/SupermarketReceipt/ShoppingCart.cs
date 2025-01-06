using System.Collections.Generic;
using System.Globalization;

namespace SupermarketReceipt
{
    public class ShoppingCart
    {
        private readonly List<ProductQuantity> _items = new List<ProductQuantity>();
        private readonly Dictionary<Product, double> _productQuantities = new Dictionary<Product, double>();
        private static readonly CultureInfo Culture = CultureInfo.CreateSpecificCulture("en-GB");
        
        /// <summary>
        /// Gets the list of items in the shopping cart.
        /// </summary>
        /// <returns>A new list containing the items in the shopping cart.</returns>
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
            if (_productQuantities.ContainsKey(product))
            {
                _productQuantities[product] += quantity;
            }
            else
            {
                _productQuantities[product] = quantity;
            }

            _items.Add(new ProductQuantity(product, quantity));
        }

        public void HandleOffers(Receipt receipt, Dictionary<Product, Offer> offers, SupermarketCatalog catalog)
        {
            foreach (var p in _productQuantities.Keys)
            {
                if (!offers.ContainsKey(p)) continue;

                var quantity = _productQuantities[p];
                var quantityAsInt = (int)quantity;
                var offer = offers[p];
                var unitPrice = catalog.GetUnitPrice(p);

                int x = offer.OfferType switch
                {
                    SpecialOfferType.ThreeForTwo => 3,
                    SpecialOfferType.TwoForAmount => 2,
                    SpecialOfferType.FiveForAmount => 5,
                    _ => 1
                };

                var numberOfXs = quantityAsInt / x;
                var remainder = quantityAsInt % x;
                double discountAmount = 0.0;

                switch (offer.OfferType)
                {
                    case SpecialOfferType.TwoForAmount when quantityAsInt >= 2:
                        discountAmount = unitPrice * quantity - (offer.Argument * numberOfXs + remainder * unitPrice);
                        break;
                    case SpecialOfferType.ThreeForTwo when quantityAsInt > 2:
                        discountAmount = quantity * unitPrice - (numberOfXs * 2 * unitPrice + remainder * unitPrice);
                        break;
                    case SpecialOfferType.TenPercentDiscount:
                        discountAmount = quantity * unitPrice * offer.Argument / 100.0;
                        break;
                    case SpecialOfferType.FiveForAmount when quantityAsInt >= 5:
                        discountAmount = unitPrice * quantity - (offer.Argument * numberOfXs + remainder * unitPrice);
                        break;
                }

                if (discountAmount != 0.0)
                {
                    var description = offer.OfferType switch
                    {
                        SpecialOfferType.TwoForAmount => $"2 for {PrintPrice(offer.Argument)}",
                        SpecialOfferType.ThreeForTwo => "3 for 2",
                        SpecialOfferType.TenPercentDiscount => $"{offer.Argument}% off",
                        SpecialOfferType.FiveForAmount => $"{x} for {PrintPrice(offer.Argument)}",
                        _ => string.Empty
                    };
                    receipt.AddDiscount(new Discount(p, description, -discountAmount));
                }
            }
        }

        private string PrintPrice(double price)
        {
            return price.ToString("N2", Culture);
        }
    }
}
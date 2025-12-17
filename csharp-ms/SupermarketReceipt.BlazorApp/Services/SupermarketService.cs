using SupermarketReceipt;

namespace SupermarketReceipt.BlazorApp.Services
{
    public class SupermarketService
    {
        private readonly FakeCatalog _catalog;
        private readonly Teller _teller;
        private ShoppingCart _cart;
        
        public SupermarketService()
        {
            _catalog = new FakeCatalog();
            _teller = new Teller(_catalog);
            _cart = new ShoppingCart();
            
            // Initialize with some sample products 🦜
            InitializeCatalog();
        }
        
        private void InitializeCatalog()
        {
            var toothbrush = new Product("Parrot Toothbrush 🦜", ProductUnit.Each);
            _catalog.AddProduct(toothbrush, 0.99);
            
            var apples = new Product("Tropical Apples 🦜", ProductUnit.Kilo);
            _catalog.AddProduct(apples, 1.99);
            
            var rice = new Product("Parrot Rice 🦜", ProductUnit.Each);
            _catalog.AddProduct(rice, 2.49);
            
            var toothpaste = new Product("Parrot Toothpaste 🦜", ProductUnit.Each);
            _catalog.AddProduct(toothpaste, 1.79);
            
            var cherryTomatoes = new Product("Cherry Tomatoes 🦜", ProductUnit.Each);
            _catalog.AddProduct(cherryTomatoes, 0.69);
            
            var milk = new Product("Parrot Milk 🦜", ProductUnit.Each);
            _catalog.AddProduct(milk, 1.29);
        }
        
        public SupermarketCatalog GetCatalog() => _catalog;
        
        public ShoppingCart GetCart() => _cart;
        
        public void ResetCart()
        {
            _cart = new ShoppingCart();
        }
        
        public void AddToCart(Product product, double quantity)
        {
            _cart.AddItemQuantity(product, quantity);
        }
        
        public void AddSpecialOffer(SpecialOfferType offerType, Product product, double argument)
        {
            _teller.AddSpecialOffer(offerType, product, argument);
        }
        
        public Receipt CheckOut()
        {
            var receipt = _teller.ChecksOutArticlesFrom(_cart);
            return receipt;
        }
        
        public List<Product> GetAllProducts()
        {
            return new List<Product>
            {
                new Product("Parrot Toothbrush 🦜", ProductUnit.Each),
                new Product("Tropical Apples 🦜", ProductUnit.Kilo),
                new Product("Parrot Rice 🦜", ProductUnit.Each),
                new Product("Parrot Toothpaste 🦜", ProductUnit.Each),
                new Product("Cherry Tomatoes 🦜", ProductUnit.Each),
                new Product("Parrot Milk 🦜", ProductUnit.Each)
            };
        }
        
        public double GetProductPrice(Product product)
        {
            return _catalog.GetUnitPrice(product);
        }
    }
}

import { SupermarketCatalog } from '../model/SupermarketCatalog';
import { Product } from '../model/Product';

export class InMemoryCatalog implements SupermarketCatalog {
    private products: Map<string, Product> = new Map();
    private prices: Map<string, number> = new Map();

    addProduct(product: Product, price: number): void {
        this.products.set(product.name, product);
        this.prices.set(product.name, price);
    }

    getUnitPrice(product: Product): number {
        return this.prices.get(product.name) || 0;
    }

    getAllProducts(): Product[] {
        return Array.from(this.products.values());
    }
}

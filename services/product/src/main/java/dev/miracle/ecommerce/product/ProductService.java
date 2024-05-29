package dev.miracle.ecommerce.product;

import dev.miracle.ecommerce.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public Integer createProduct(ProductRequest request) {
            var product = mapper.toProduct(request);
            return productRepository.save(product).getId();
    }

    public ProductResponse findById(Integer productId) {
        return productRepository.findById(productId)
                    .map(mapper::toProductResponse)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with the ID::" + productId));
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> requests) {
        var productIds = requests.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();
        var storedProducts = productRepository.findAllByIdInOrderById(productIds);
        if (productIds.size() != storedProducts.size())
            throw new ProductPurchaseException("One or more product does not exist");
        var storedRequest = requests
                .stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();
        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();

        for (int i = 0; i < storedProducts.size(); i++) {
            var storedProduct = storedProducts.get(i);
            var requestProduct = storedRequest.get(i);
            if (storedProduct.getAvailableQuantity() < requestProduct.quantity()) {
                throw new ProductPurchaseException("Product with ID::" + storedProduct.getId() + " does not have enough quantity");
            }
            var newAvailableQuantity = storedProduct.getAvailableQuantity() - requestProduct.quantity();
            storedProduct.setAvailableQuantity(newAvailableQuantity);
            productRepository.save(storedProduct);
            purchasedProducts.add(mapper.toProductPurchaseReponse(storedProduct, requestProduct.quantity()));
        }
        return purchasedProducts;
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }
}

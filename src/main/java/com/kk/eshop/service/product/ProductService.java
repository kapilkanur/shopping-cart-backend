package com.kk.eshop.service.product;

import com.kk.eshop.dtos.ProductDTO;
import com.kk.eshop.dtos.ProductUpdateDTO;
import com.kk.eshop.exceptions.ProductNotFoundException;
import com.kk.eshop.model.Category;
import com.kk.eshop.model.Product;
import com.kk.eshop.service.repository.CategoryRepository;
import com.kk.eshop.service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product addProduct(final ProductDTO productDTO) {
        Category category = Optional.ofNullable(categoryRepository.findByName(productDTO.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(productDTO.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        productDTO.setCategory(category);
        return productRepository.save(createProduct(productDTO, category));
    }

    private Product createProduct(final ProductDTO productDTO, final Category category) {
        return new Product(
                productDTO.getName(),
                productDTO.getBrand(),
                productDTO.getPrice(),
                productDTO.getQuantityInInventory(),
                productDTO.getDescription(),
                category
        );
    }

    @Override
    public Product getProductById(final Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(final String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrandName(final String brand) {
        return productRepository.findByBrandName(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(final String category, final String brand) {
        return productRepository.findByCategoryNameAndBrandName(category, brand);
    }

    @Override
    public List<Product> getProductsByName(final String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(final String brand, final String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public void deleteProductById(final Long productId) {
        productRepository.findById(productId)
                .ifPresentOrElse(productRepository::delete, () -> {
                    throw new ProductNotFoundException("Product not found");
                });
    }

    @Override
    public Product updateProductById(final ProductUpdateDTO productUpdateDTO, final Long productId) {
        return productRepository.findById(productId)
                .map(product -> updateProduct(product, productUpdateDTO))
                .map(productRepository :: save)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    private Product updateProduct(final Product product, final ProductUpdateDTO productUpdateDTO) {
        product.setName(productUpdateDTO.getName());
        product.setBrand(productUpdateDTO.getBrand());
        product.setPrice(productUpdateDTO.getPrice());
        product.setQuantityInInventory(productUpdateDTO.getQuantityInInventory());
        product.setDescription(productUpdateDTO.getDescription());

        Category category = Optional.ofNullable(categoryRepository.findByName(productUpdateDTO.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(productUpdateDTO.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        product.setCategory(category);

        return product;
    }

    @Override
    public Long countProductsByBrandAndName(final String brand, final String name) {
        return productRepository.countByBrandAndName(brand, name);
    }
}

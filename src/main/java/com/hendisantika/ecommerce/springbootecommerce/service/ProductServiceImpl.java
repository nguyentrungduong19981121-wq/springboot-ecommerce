package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.CreateProductRequest;
import com.hendisantika.ecommerce.springbootecommerce.dto.ProductDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.UpdateProductRequest;
import com.hendisantika.ecommerce.springbootecommerce.exception.ResourceNotFoundException;
import com.hendisantika.ecommerce.springbootecommerce.model.Category;
import com.hendisantika.ecommerce.springbootecommerce.model.Product;
import com.hendisantika.ecommerce.springbootecommerce.model.ProductImage;
import com.hendisantika.ecommerce.springbootecommerce.repository.CategoryRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.ProductImageRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2019-04-26
 * Time: 06:16
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    public ProductServiceImpl(ProductRepository productRepository, 
                             CategoryRepository categoryRepository,
                             ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<ProductDto> getAllProductsDto() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductDto(Long id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDto(product);
    }

    @Override
    public ProductDto createProduct(CreateProductRequest request) {
        // Validate unique slug
        if (productRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Product with slug '" + request.getSlug() + "' already exists");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(request.getSlug());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock() != null ? request.getStock() : 0);
        product.setPictureUrl(request.getPictureUrl());

        // Set category if provided
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        // Save product first
        Product savedProduct = productRepository.save(product);

        // Add images if provided
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String url : request.getImageUrls()) {
                ProductImage image = new ProductImage();
                image.setUrl(url);
                image.setProduct(savedProduct);
                savedProduct.addImage(image);
            }
            savedProduct = productRepository.save(savedProduct);
        }

        return convertToDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update fields if provided
        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getSlug() != null && !request.getSlug().equals(product.getSlug())) {
            // Validate unique slug
            if (productRepository.existsBySlug(request.getSlug())) {
                throw new IllegalArgumentException("Product with slug '" + request.getSlug() + "' already exists");
            }
            product.setSlug(request.getSlug());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }

        if (request.getPictureUrl() != null) {
            product.setPictureUrl(request.getPictureUrl());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        // Update images if provided
        if (request.getImageUrls() != null) {
            // Remove existing images
            product.getImages().clear();
            productImageRepository.deleteByProductId(id);

            // Add new images
            for (String url : request.getImageUrls()) {
                ProductImage image = new ProductImage();
                image.setUrl(url);
                image.setProduct(product);
                product.addImage(image);
            }
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    public List<ProductDto> searchProducts(String name, Long categoryId, String sortBy) {
        List<Product> products;

        // Handle search and filter combinations
        if (name != null && !name.trim().isEmpty() && categoryId != null) {
            // Search by name AND filter by category
            products = productRepository.searchByNameAndCategoryId(name.trim(), categoryId);
        } else if (name != null && !name.trim().isEmpty()) {
            // Search by name only
            products = productRepository.searchByName(name.trim());
        } else if (categoryId != null) {
            // Filter by category only (with optional sorting)
            if ("price_asc".equals(sortBy)) {
                products = productRepository.findByCategoryIdOrderByPriceAsc(categoryId);
            } else if ("price_desc".equals(sortBy)) {
                products = productRepository.findByCategoryIdOrderByPriceDesc(categoryId);
            } else {
                products = productRepository.findByCategoryId(categoryId);
            }
        } else {
            // No filter, just sort
            if ("price_asc".equals(sortBy)) {
                products = productRepository.findAllByOrderByPriceAsc();
            } else if ("price_desc".equals(sortBy)) {
                products = productRepository.findAllByOrderByPriceDesc();
            } else {
                products = productRepository.findAll();
            }
        }

        // Manual sorting if needed (for search results)
        if ((name != null && !name.trim().isEmpty()) && sortBy != null) {
            if ("price_asc".equals(sortBy)) {
                products.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
            } else if ("price_desc".equals(sortBy)) {
                products.sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
            }
        }

        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setPictureUrl(product.getPictureUrl());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList());
        dto.setImageUrls(imageUrls);

        return dto;
    }
}
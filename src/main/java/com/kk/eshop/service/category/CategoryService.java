package com.kk.eshop.service.category;

import com.kk.eshop.exceptions.CategoryNotFoundException;
import com.kk.eshop.exceptions.ResourceAlreadyExistsException;
import com.kk.eshop.model.Category;
import com.kk.eshop.service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category addCategory(final Category category) {
        return Optional.of(category).filter(c -> !categoryRepository.existsByName(c.getName()))
                .map(categoryRepository :: save)
                .orElseThrow(() -> new ResourceAlreadyExistsException(category.getName() + " already exists!"));
    }

    @Override
    public Category getCategoryById(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found!"));
    }

    @Override
    public Category getCategoryByName(final String categoryName) {
        return categoryRepository.findByName(categoryName);
    }

    @Override
    public Category updateCategoryById(final Category category, final Long categoryId) {
        return Optional.ofNullable(getCategoryById(categoryId))
                .map(oldCategory -> {
                    oldCategory.setName(category.getName());
                    return categoryRepository.save(oldCategory);
                }).orElseThrow(() -> new CategoryNotFoundException("Category not found!"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategoryById(final Long categoryId) {
        categoryRepository.findById(categoryId)
                .ifPresentOrElse(categoryRepository::delete, () -> {
                    throw new CategoryNotFoundException("Category not found!");
                });
    }
}

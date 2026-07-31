package com.shoaib.aiservice.category;

import com.shoaib.aiservice.exception.CategoryResolutionException;
import com.shoaib.aiservice.util.FixoraCategory;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StaticCategoryResolverTest {
    private final StaticCategoryResolver resolver = new StaticCategoryResolver();

    @Test
    void resolvesActiveCategoryFromStaticMapping() {
        assertEquals(UUID.fromString("d9122178-a95f-40fc-ac5b-9e773c4c46fd"),
                resolver.resolve(FixoraCategory.PLUMBER));
    }

    @Test
    void unsupportedCategoryHasNoCategoryId() {
        assertNull(resolver.resolve(FixoraCategory.UNSUPPORTED));
    }

    @Test
    void generalInformationCategoryHasNoCategoryId() {
        assertNull(resolver.resolve(FixoraCategory.GENERAL_INFORMATION));
    }

    @Test
    void nullCategoryHasNoCategoryId() {
        assertNull(resolver.resolve(null));
    }

    @Test
    void unmappedBookableCategoryIsRejected() {
        StaticCategoryResolver emptyResolver = new StaticCategoryResolver(Map.of());

        CategoryResolutionException exception = assertThrows(CategoryResolutionException.class,
                () -> emptyResolver.resolve(FixoraCategory.PLUMBER));

        assertEquals("No static category mapping exists for bookable category PLUMBER", exception.getMessage());
    }
}

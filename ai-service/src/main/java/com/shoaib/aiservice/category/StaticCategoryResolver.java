package com.shoaib.aiservice.category;

import com.shoaib.aiservice.exception.CategoryResolutionException;
import com.shoaib.aiservice.util.FixoraCategory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class StaticCategoryResolver implements CategoryResolver {
    private static final Map<FixoraCategory, UUID> CATEGORY_IDS = Map.ofEntries(
            Map.entry(FixoraCategory.PLUMBER, UUID.fromString("d9122178-a95f-40fc-ac5b-9e773c4c46fd")),
            Map.entry(FixoraCategory.ELECTRICIAN, UUID.fromString("7f7c1a98-3897-4e30-8fcd-8987d51b6c71")),
            Map.entry(FixoraCategory.CARPENTER, UUID.fromString("897d4567-87e5-4ef3-9dbf-a013697b3853")),
            Map.entry(FixoraCategory.PAINTER, UUID.fromString("a280cb8e-854f-4fa3-9512-393ab80afdf6")),
            Map.entry(FixoraCategory.CLEANER, UUID.fromString("42e36fec-6d7c-489c-aa7a-6fd5976c19d6")),
            Map.entry(FixoraCategory.WATER_PURIFIER, UUID.fromString("7f14635f-7730-4014-a24a-c7613ed9ca0e")),
            Map.entry(FixoraCategory.AC, UUID.fromString("2c12c382-cedd-4b5b-aad2-8f2fbc9372af")),
            Map.entry(FixoraCategory.WOMEN_SALON, UUID.fromString("878e88ee-4429-4c9a-81f4-874062625318")),
            Map.entry(FixoraCategory.MEN_SALON, UUID.fromString("a5f1abea-9c7b-40d1-a06b-94a95e384c5a"))
    );

    private final Map<FixoraCategory, UUID> categoryIds;

    public StaticCategoryResolver() {
        this(CATEGORY_IDS);
    }

    StaticCategoryResolver(Map<FixoraCategory, UUID> categoryIds) {
        this.categoryIds = Map.copyOf(categoryIds);
    }

    @Override
    public UUID resolve(FixoraCategory category) {
        if (category == null || category == FixoraCategory.UNSUPPORTED
                || category == FixoraCategory.GENERAL_INFORMATION) {
            return null;
        }

        UUID categoryId = categoryIds.get(category);
        if (categoryId == null) {
            throw new CategoryResolutionException("No static category mapping exists for bookable category " + category);
        }
        return categoryId;
    }
}

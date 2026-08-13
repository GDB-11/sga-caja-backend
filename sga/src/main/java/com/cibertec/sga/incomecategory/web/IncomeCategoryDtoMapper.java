package com.cibertec.sga.incomecategory.web;

import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import com.cibertec.sga.incomecategory.web.dto.IncomeCategoryResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link IncomeCategory} y los DTOs de {@code web}.
 */
@Component
public class IncomeCategoryDtoMapper {

    public IncomeCategoryResponse toResponse(IncomeCategory incomeCategory) {
        return new IncomeCategoryResponse(incomeCategory.getUuid(), incomeCategory.getName());
    }
}

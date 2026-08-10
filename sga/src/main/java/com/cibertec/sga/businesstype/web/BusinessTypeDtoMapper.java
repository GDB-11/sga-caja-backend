package com.cibertec.sga.businesstype.web;

import com.cibertec.sga.businesstype.domain.model.BusinessType;
import com.cibertec.sga.businesstype.web.dto.BusinessTypeResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link BusinessType} y los DTOs de {@code web}.
 */
@Component
public class BusinessTypeDtoMapper {

    public BusinessTypeResponse toResponse(BusinessType businessType) {
        return new BusinessTypeResponse(businessType.getUuid(), businessType.getName());
    }
}

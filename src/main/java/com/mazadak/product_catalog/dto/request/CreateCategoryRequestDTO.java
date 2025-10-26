package com.mazadak.product_catalog.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequestDTO {
    private String name;
}
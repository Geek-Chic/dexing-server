package com.company.project.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Created by evil on 10/15/17.
 */
@Data
@Builder
public class ImageHandleData {
    private Integer width;
    private Integer height;
    private Integer rotate;
}

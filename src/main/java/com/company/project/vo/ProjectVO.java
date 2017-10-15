package com.company.project.vo;

import com.company.project.model.Project;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Delegate;

/**
 * Created by evil on 10/15/17.
 */
@Builder(builderMethodName = "projectVOBuilder")
@Data
public class ProjectVO {
    @Delegate
    private Project project;
}

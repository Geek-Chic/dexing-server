package com.company.project.vo;

import com.company.project.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by evil on 10/15/17.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAreComponent {
    String area;
    List<Project> projectList;
}

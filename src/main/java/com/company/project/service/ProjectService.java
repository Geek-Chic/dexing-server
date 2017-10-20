package com.company.project.service;
import com.company.project.model.Project;
import com.company.project.core.Service;
import com.company.project.vo.ProjectAreComponent;

import java.util.List;


/**
 * Created by CodeGenerator on 2017/10/13.
 */
public interface ProjectService extends Service<Project> {

    void addProject(Project project);

    List<Project> findIndex();

    List<ProjectAreComponent> findArea();
}

package com.company.project.web;

import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.model.Project;
import com.company.project.service.ProjectService;
import com.company.project.vo.ProjectAreComponent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by CodeGenerator on 2017/10/13.
 */
@RestController
@RequestMapping("/home")
public class ProjectController {
    @Resource
    private ProjectService projectService;

    @PostMapping("/index")
    public Result list() {
        List<Project> list = projectService.findIndex();
        return ResultGenerator.genSuccessResult(list);
    }

    @PostMapping("/area")
    public Result second() {
        List<ProjectAreComponent> list = projectService.findArea();
        return ResultGenerator.genSuccessResult(list);
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam(value = "id", required = false) Integer id) {
        Project project = projectService.findById(id);
        return ResultGenerator.genSuccessResult(project);
    }

}

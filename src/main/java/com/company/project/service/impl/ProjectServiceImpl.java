package com.company.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.company.project.dao.ProjectMapper;
import com.company.project.model.Area;
import com.company.project.model.Project;
import com.company.project.service.AreaService;
import com.company.project.service.ProjectService;
import com.company.project.core.AbstractService;
import com.company.project.vo.ProjectAreComponent;
import com.company.project.vo.ProjectVO;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by CodeGenerator on 2017/10/13.
 */
@Service
@Transactional
public class ProjectServiceImpl extends AbstractService<Project> implements ProjectService {
    @Resource
    private AreaService areaService;

    @Resource
    private ProjectMapper projectMapper;

    @Override
    public void addProject(Project project) {
//        save(project);
        //oracle需要这样加
        projectMapper.insertProject(project);
    }

    @Override
    public List<Project> findIndex() {
        Condition example = new Condition(Project.class);
        example.createCriteria().andEqualTo("isTop", 1);
        return findByCondition(example);
    }

    @Override
    public List<ProjectAreComponent> findArea() {
        List<Project> allProjects = findAll();
        Map<String, List<Project>> map = new HashMap<>();
        for (Project project : allProjects) {
            if (map.containsKey(project.getArea())) {
                map.get(project.getArea()).add(project);
            } else {
                List<Project> projects = new ArrayList<Project>();
                projects.add(project);
                map.put(project.getArea(), projects);
            }
        }
        List<ProjectAreComponent> area = new ArrayList<ProjectAreComponent>();
        for (String key : map.keySet()) {
            area.add(ProjectAreComponent.builder().area(key).projectList(map.get(key)).build());
        }
        return area;
    }
}

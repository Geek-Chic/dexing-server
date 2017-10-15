package com.company.project.service.impl;

import com.company.project.dao.AreaMapper;
import com.company.project.model.Area;
import com.company.project.service.AreaService;
import com.company.project.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2017/10/15.
 */
@Service
@Transactional
public class AreaServiceImpl extends AbstractService<Area> implements AreaService {
    @Resource
    private AreaMapper areaMapper;

}

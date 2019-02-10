package com.devinkin.springcloud.service;

import com.devinkin.springcloud.entities.Dept;

import java.util.List;

public interface DeptService {

    boolean add(Dept dept);

    Dept get(Long id);

    List<Dept> list();
}

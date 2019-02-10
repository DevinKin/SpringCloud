package com.devinkin.springcloud.controller;

import com.devinkin.springcloud.entities.Dept;
import com.devinkin.springcloud.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeptController {
    @Autowired
    private DeptService deptService;


    // 服务发现
    @Autowired
    private DiscoveryClient client;

    @PostMapping(value = "/dept/add")
    public boolean add(@RequestBody Dept dept) {
        return deptService.add(dept);
    }

    @GetMapping(value = "/dept/get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return deptService.get(id);
    }

    @GetMapping(value = "/dept/list")
    public List<Dept> list() {
        return deptService.list();
    }


    @GetMapping(value = "/dept/discovery")
    public Object discovery() {
        List<String> list = client.getServices();
        System.out.println("***********" + list);

        List<ServiceInstance> srvlist = client.getInstances("MICROSERVICECLOUD-DEPT");
        for (ServiceInstance e : srvlist) {
            System.out.println(e.getServiceId() + "\t" + e.getHost() + "\t" +
                    e.getPort() + "\t" + e.getUri());
        }
        return this.client;
    }
}

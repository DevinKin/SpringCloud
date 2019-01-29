package com.devinkin.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
public class Dept implements Serializable {
    private Integer id;             // 主键
    private String dname;           // 部门名称
    private String db_source;       // 来自哪个数据库。

}

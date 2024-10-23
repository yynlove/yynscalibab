package com.yyn.mq.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @description table1
 * @author BEJSON
 * @date 2024-10-13
 */
@Data
@TableName(value="table1")
public class Table1 implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * test1
     */
    private String test1;

    /**
     * test2
     */
    private String test2;

    /**
     * test3
     */
    private String test3;

    /**
     * test4
     */
    private String test4;

    /**
     * test5
     */
    private Integer test5;


}
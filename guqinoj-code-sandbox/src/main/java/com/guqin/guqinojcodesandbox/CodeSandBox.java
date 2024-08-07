package com.guqin.guqinojcodesandbox;


import com.guqin.guqinojcodesandbox.model.ExecuteCodeRequest;
import com.guqin.guqinojcodesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口
 * @author 顾琴
 */
public interface CodeSandBox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}

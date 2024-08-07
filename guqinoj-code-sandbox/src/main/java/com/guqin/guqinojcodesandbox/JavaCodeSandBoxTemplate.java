package com.guqin.guqinojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.guqin.guqinojcodesandbox.model.ExecuteCodeRequest;
import com.guqin.guqinojcodesandbox.model.ExecuteCodeResponse;
import com.guqin.guqinojcodesandbox.model.ExecuteMessage;
import com.guqin.guqinojcodesandbox.model.JudgeInfo;
import com.guqin.guqinojcodesandbox.security.DefaultSecurityManager;
import com.guqin.guqinojcodesandbox.utils.ProcessUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java代码沙箱实现
 */
@Slf4j
public abstract class JavaCodeSandBoxTemplate implements CodeSandBox{

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 5000L;


    //取用是最大值，便于判断是否超时
    private static long maxTime = 0;

    private static final String userCodeParentPathName =
            System.getProperty("user.dir") + File.separator + GLOBAL_CODE_DIR_NAME
                    + File.separator + UUID.randomUUID();
    /**
     * 保存代码文件
     * @param code 用户代码
     * @return
     */
    public File saveCodeToFile(String code) {

        String userDir = System.getProperty("user.dir");

        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

        //判断全局代码目录是否存在
        if (FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        //把用户的代码隔离存放
        String userCodeParentPathName = globalCodePathName + File.separator + UUID.randomUUID();

        return FileUtil.writeString(code, userCodeParentPathName
                + File.separator + GLOBAL_JAVA_CLASS_NAME, "UTF-8");
    }


    /**
     * 编译代码
     * @param userCodeFile
     * @return
     */
    public ExecuteMessage  compileFile(File userCodeFile){
        String compileCmd = String.format("javac -encoding utf-8 %s",userCodeFile.getAbsolutePath());

        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            return ProcessUtil.runProcessAndGetMessage(compileProcess, "编译");

        } catch (IOException e) {
//            return this.getErrorResponse(e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 执行代码文件，得到输出结果
     * @param userCodeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runFile(File userCodeFile,List<String> inputList) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();



        for (String inputArgs : inputList) {
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPathName,inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                //超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        if (runProcess.isAlive()) {
                            System.out.println("超时啦，中断");
                            runProcess.destroy();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtil.runInteractProcessAndGetMessage(runProcess, "运行", inputArgs);
                executeMessageList.add(executeMessage);
                if (executeMessage.getTime() != null) {
                    long time = executeMessage.getTime();
                    maxTime = Math.max(maxTime, time);
                }
            } catch (IOException e) {
                throw new RuntimeException("程序执行异常");
            }
        }
        return executeMessageList;
    }


    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if(StrUtil.isNotBlank(errorMessage)){
                executeCodeResponse.setMessage(errorMessage);
                //执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
        }
        //正常运行完成
        if(outputList.size() == executeMessageList.size()){
            executeCodeResponse.setStatus(1);
        }
        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        // 要借助第三方库来获取内存占用，非常麻烦，此处不做实现
//        jdgeInfo.setMemory();
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    /**
     * 文件清理
     * @param
     * @return
     */
    public boolean deleteFile(File userCodeFile) {
        if(userCodeFile.getParentFile() != null){
            return FileUtil.del(userCodeParentPathName);
        }
        return true;
    }


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();


        //1. 把用户的代码保存为文件
        File userCodeFile = saveCodeToFile(code);

        //2. 编译代码，得到class文件
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFile);

        //3. 执行代码，得到输出结果
        List<ExecuteMessage> executeMessageList = runFile(userCodeFile, inputList);

        //4. 收集整理输出结果
        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);

        //5. 文件清理
        boolean b = deleteFile(userCodeFile);
        if(!b){
            log.error("文件清理失败");
        }

        return outputResponse;
    }

        /**
         * 获取错误响应
         * @param e
         * @return
         */
        public ExecuteCodeResponse getErrorResponse(Throwable e){

            //6. 错误处理，提升程序健壮性
            ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();

            executeCodeResponse.setOutputList(new ArrayList<>());
            executeCodeResponse.setMessage(e.getMessage());
            //“2” 表示代码沙箱错误
            executeCodeResponse.setStatus(2);
            executeCodeResponse.setJudgeInfo(new JudgeInfo());

            return executeCodeResponse;

        }

}

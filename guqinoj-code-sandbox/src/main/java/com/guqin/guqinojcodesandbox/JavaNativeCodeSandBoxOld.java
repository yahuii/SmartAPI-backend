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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 代码沙箱接口
 * @author 顾琴
 */
public class JavaNativeCodeSandBoxOld {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";


    private static final long TIME_OUT = 5000L;

    private static final List<String> BLACK_LIST = Arrays.asList("Files","exec");

    private static final WordTree WORD_TREE;
    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager";
    private static final String SECURITY_MANAGER_PATH = "D:\\code\\huiapi-backend\\guqinoj-code-sandbox\\src\\main\\resources\\security";

    static{
        //初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(BLACK_LIST);
    }

    public static void main(String[] args) {
        JavaNativeCodeSandBoxOld javaNativeCodeSandBox = new JavaNativeCodeSandBoxOld();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2"));
        String code = ResourceUtil.readStr("unsafeCode/ReadFileError.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");


        javaNativeCodeSandBox.executeCode(executeCodeRequest);
    }

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){

        System.setSecurityManager(new DefaultSecurityManager());

        //1. 把用户的代码保存为文件

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();


        //查找是否含有敏感词
        FoundWord foundWord = WORD_TREE.matchWord(code);

        if(foundWord != null){
            System.out.println(foundWord);
            return null;
        }


        String userDir = System.getProperty("user.dir");

        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

        //判断全局代码目录是否存在
        if(FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }

        //把用户的代码隔离存放
        String userCodeParentPathName = globalCodePathName + File.separator + UUID.randomUUID();

        File userCodeFile = FileUtil.writeString(code, userCodeParentPathName
                        + File.separator + GLOBAL_JAVA_CLASS_NAME, "UTF-8");


        //2. 编译代码，得到class文件

        String compileCmd = String.format("javac -encoding utf-8 %s",userCodeFile.getAbsolutePath());

        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeMessage);

        } catch (IOException e) {
            return this.getErrorResponse(e);
        }

        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        //取用是最大值，便于判断是否超时
        long maxTime = 0;


        //3. 执行代码，得到输出结果
        for (String inputArgs : inputList) {

            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main %s",userCodeParentPathName,SECURITY_MANAGER_PATH,SECURITY_MANAGER_CLASS_NAME,inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);

                //超时控制
                new Thread(()->{
                    try {
                        Thread.sleep(TIME_OUT);
                        if(runProcess.isAlive()){
                            System.out.println("超时啦，中断");
                            runProcess.destroy();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtil.runInteractProcessAndGetMessage(runProcess, "运行",inputArgs);
                executeMessageList.add(executeMessage);

                if(executeMessage.getTime() != null){
                    long time = executeMessage.getTime();
                    maxTime = Math.max(maxTime, time);
                }

            } catch (IOException e) {
                return this.getErrorResponse(e);
            }
        }

        //4. 收集整理输出结果
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
//        judgeInfo.setMemory();

        executeCodeResponse.setJudgeInfo(judgeInfo);


        //5. 文件清理
        if(userCodeFile.getParentFile() != null){
            boolean del = FileUtil.del(userCodeParentPathName);
            System.out.println("删除" + ( del ? "成功" : "失败" ));
            
        }
        
        return executeCodeResponse;
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

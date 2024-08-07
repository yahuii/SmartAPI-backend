package com.guqin.guqinojcodesandbox.utils;

import com.guqin.guqinojcodesandbox.model.ExecuteMessage;
import org.springframework.util.StopWatch;

import java.io.*;

public class ProcessUtil {
    /**
     * 执行进程并获取信息
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess,String opName){

        ExecuteMessage executeMessage = new ExecuteMessage();

        try {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            int exitValue  = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);



            //正常退出
            if(exitValue == 0){
                System.out.println(opName + "成功");
                //读取编译后输出信息
                BufferedReader bufferedReader = new BufferedReader
                        (new InputStreamReader(runProcess.getInputStream()));
                StringBuilder compileOutputStringBuilder = new StringBuilder();

                String compileOutLine;
                while( (compileOutLine = bufferedReader.readLine()) != null){
                    compileOutputStringBuilder.append(compileOutLine);
                }
                executeMessage.setMessage(compileOutputStringBuilder.toString());

            }
            //异常退出
            else{
                System.out.println(opName + "失败，错误码：" + exitValue);
                //分批读取编译后正常输出信息
                BufferedReader bufferedReader = new BufferedReader
                        (new InputStreamReader(runProcess.getInputStream()));

                StringBuilder compileOutputStringBuilder = new StringBuilder();
                String compileOutLine;
                while( (compileOutLine = bufferedReader.readLine()) != null){
                    compileOutputStringBuilder.append(compileOutLine);
                }
                executeMessage.setMessage(compileOutputStringBuilder.toString());


                StringBuilder errorOutputStringBuilder = new StringBuilder();
                //分批读取编译后输出信息
                BufferedReader errorBufferedReader = new BufferedReader
                        (new InputStreamReader(runProcess.getErrorStream()));
                String errorCompileOutLine;
                while( (errorCompileOutLine = errorBufferedReader.readLine()) != null){
                    errorOutputStringBuilder.append(errorCompileOutLine);
                }
                executeMessage.setErrorMessage(errorOutputStringBuilder.toString());
            }

            stopWatch.stop();

            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return executeMessage;
    }


    /**
     * 执行交互式进程并获取信息
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess,String opName,String args){

        ExecuteMessage executeMessage = new ExecuteMessage();

        try {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();


            InputStream inputStream = runProcess.getInputStream();
            OutputStream outputStream = runProcess.getOutputStream();

            //交互式程序（需要输入的程序），需要定义一个writer进行写入
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(args);
            outputStreamWriter.flush();
            BufferedReader bufferedReader = new BufferedReader
                    (new InputStreamReader(runProcess.getInputStream()));
            StringBuilder compileOutputStringBuilder = new StringBuilder();


            //分批获取正常的输入
            String compileOutLine;
            while( (compileOutLine = bufferedReader.readLine()) != null){
                compileOutputStringBuilder.append(compileOutLine);
            }
            executeMessage.setMessage(compileOutputStringBuilder.toString());

            int exitValue  = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);

            //资源回收

            inputStream.close();
            outputStreamWriter.close();
            outputStream.close();
            runProcess.destroy();

            stopWatch.stop();

            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return executeMessage;
    }
}

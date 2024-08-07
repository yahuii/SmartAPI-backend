package com.guqin.guqinojcodesandbox;


import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.guqin.guqinojcodesandbox.model.ExecuteCodeRequest;
import com.guqin.guqinojcodesandbox.model.ExecuteCodeResponse;
import com.guqin.guqinojcodesandbox.model.ExecuteMessage;
import com.guqin.guqinojcodesandbox.model.JudgeInfo;
import com.guqin.guqinojcodesandbox.utils.ProcessUtil;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 代码沙箱接口
 * @author 顾琴
 */
public class JavaDockerCodeSandBox extends JavaCodeSandBoxTemplate {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";


    private static final long TIME_OUT = 5000L;

    private static final List<String> BLACK_LIST = Arrays.asList("Files","exec");

    private static final WordTree WORD_TREE;

    private static final Boolean FIRST_INIT = true;


    static{
        //初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(BLACK_LIST);
    }

    /**
     * 创建容器，把文件复制到容器中
     * @param userCodeFile
     * @param inputList
     * @return
     */
    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {

        String userCodeParentPathName = userCodeFile.getParentFile().getAbsolutePath();

        //获取默认的dockerClient
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        String image = "openjdk:8-alpine";

        if(FIRST_INIT){
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback(){
                @Override
                public void onNext(PullResponseItem item){
                    System.out.println("下载镜像" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd.exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
        }

        System.out.println("下载完成");

        //创建容器
        HostConfig hostConfig = new HostConfig();
        hostConfig.setBinds(new Bind(userCodeParentPathName,new Volume("/app")));
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);

        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .exec();

        String containerId = createContainerResponse.getId();

        //启动容器
        dockerClient.startContainerCmd(containerId).exec();

        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        final long[] maxMemory = {0L};




        for (String inputArgs : inputList) {
            String[] inputArgsArray = inputArgs.split(" ");

            //docker exec funny_wozniak java -cp /app Main 1 3
            String[] cmdArray = ArrayUtil.append(new String[]{"java","-cp","/app","Main"},inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();


            ExecuteMessage executeMessage = new ExecuteMessage();

            final String[] message = {null};
            final String[] errMessage = {null};



            //获取占用内存
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);

            statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(maxMemory[0],statistics.getMemoryStats().getUsage());
                }
                @Override
                public void close() throws IOException {

                }

                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }
            });

            final boolean[] isTimeOut = {true};
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback(){

                @Override
                public void onComplete() {
                    isTimeOut[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if(streamType == StreamType.STDERR){
                        errMessage[0] = new String(frame.getPayload());
                        System.out.println("输出错误结果" + new String(frame.getPayload()));

                    }
                    else{
                        message[0] = new String(frame.getPayload());
                        System.out.println("输出结果" + new String(frame.getPayload()));
                    }
                    super.onNext(frame);
                }
            };
            System.out.println("创建执行命令"+execCreateCmdResponse);
            String execId = execCreateCmdResponse.getId();


            long time = 0L;
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                statsCmd.close();
                time = stopWatch.getLastTaskTimeMillis();
            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }

            executeMessage.setErrorMessage(errMessage[0]);
            executeMessage.setMessage(message[0]);
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);

            executeMessageList.add(executeMessage);

        }

        return executeMessageList;
    }

}

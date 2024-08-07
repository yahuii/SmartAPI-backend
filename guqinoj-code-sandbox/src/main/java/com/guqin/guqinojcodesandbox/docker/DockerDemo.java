package com.guqin.guqinojcodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;

public class DockerDemo {
    public static void main(String[] args) throws InterruptedException {
        //获取默认的dockerClient
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

//        PingCmd pingCmd = dockerClient.pingCmd();
//        pingCmd.exec();

        String image = "nginx:latest";
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback(){
            @Override
            public void onNext(PullResponseItem item){
                System.out.println("下载镜像");
                super.onNext(item);
            }
        };
        pullImageCmd.exec(pullImageResultCallback)
                .awaitCompletion();


    }
}

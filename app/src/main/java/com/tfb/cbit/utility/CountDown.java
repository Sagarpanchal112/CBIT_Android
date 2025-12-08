package com.tfb.cbit.utility;

public abstract class CountDown {

    long totalTime = 0;
    long tickTime = 0;

    Thread thread;
    boolean canceled = false;

    public CountDown(long totalTime,long tickTime){
        this.totalTime = totalTime;
        this.tickTime = tickTime;
    }

    public abstract void onTick(long remainingMillSec);

    public abstract void onFinish();

    public void start(){

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // Do in thread

                canceled = false;

                for (long elapsedTime = 0; elapsedTime < totalTime; elapsedTime += tickTime) {

                    if(!canceled){

                        onTick(totalTime-elapsedTime);
                        try {
                            thread.sleep(tickTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }else{
                        break;
                    }
                }

                if(!canceled){
                    onFinish();
                }

            }

        });

        thread.start();
    }

    public void cancel(){
        canceled = true;
    }
}

package com.tfb.cbit.models;

import java.util.ArrayList;
import java.util.List;

public class SlotDataModel {

    /*
     * gameAnsType = 0 (-100 to 100)
     * gameAnsType = 1 (-10 to 10)
     * gameAnsType = 2 (0 to 9)
     */
    private int gameAnsType = 0;
    public SlotDataModel(int gameAnsType){
        this.gameAnsType = gameAnsType;
    }

    public List<String> get2Slot(){
        List<String> list = new ArrayList<>();
        switch (gameAnsType){
            case 0:
                list.add("-100 to 0");
                list.add("1 to 100");
                break;
            case 1:
                list.add("-10 to 0");
                list.add("1 to 10");
                break;
            case 2:
                list.add("0 to 4");
                list.add("5 to 9");
                break;
        }
        return list;
    }

    public List<String> get3Slot(){
        List<String> list = new ArrayList<>();
        switch (gameAnsType){
            case 0:
                list.add("-100 to -34");
                list.add("-33 to 32");
                list.add("33 to 100");
                break;
            case 1:
                list.add("-10 to -4");
                list.add("-3 to 2");
                list.add("3 to 10");
                break;
            case 2:
                list.add("0 to 3");
                list.add("4 to 6");
                list.add("7 to 9");
                break;
        }
        return list;
    }

    public List<String> get4Slot(){
        List<String> list = new ArrayList<>();
        switch (gameAnsType){
            case 0:
                list.add("-100 to -50");
                list.add("-49 to 0");
                list.add("1 to 50");
                list.add("51 to 100");
                break;
            case 1:
                list.add("-10 to -5");
                list.add("-4 to 0");
                list.add("1 to 5");
                list.add("6 to 10");
                break;
            case 2:
                list.add("0 to 2");
                list.add("3 to 4");
                list.add("5 to 6");
                list.add("7 to 9");
                break;
        }
        return list;
    }

    public List<String> get5Slot(){
        List<String> list = new ArrayList<>();
        switch (gameAnsType){
            case 0:
                list.add("-100 to -60");
                list.add("-59 to -20");
                list.add("-19 to 20");
                list.add("21 to 60");
                list.add("61 to 100");
                break;
            case 1:
                list.add("-10 to -6");
                list.add("-5 to -2");
                list.add("-1 to 2");
                list.add("3 to 6");
                list.add("7 to 10");
                break;
            case 2:
                list.add("0 to 1");
                list.add("2 to 2");
                list.add("3 to 3");
                list.add("4 to 4");
                list.add("5 to 9");
                break;
        }
        return list;
    }

    public List<String> get6Slot(){
        List<String> list = new ArrayList<>();
        switch (gameAnsType){
            case 0:
                list.add("-100 to -67");
                list.add("-66 to -34");
                list.add("-33 to -1");
                list.add("0 to 32");
                list.add("33 to 65");
                list.add("66 to 100");
                break;
            case 1:
                list.add("-10 to -7");
                list.add("-6 to -4");
                list.add("-3 to -1");
                list.add("0 to 2");
                list.add("3 to 5");
                list.add("6 to 10");
                break;
            case 2:
                list.add("0 to 1");
                list.add("2 to 2");
                list.add("3 to 3");
                list.add("4 to 4");
                list.add("5 to 5");
                list.add("6 to 9");
                break;
        }
        return list;
    }

    public List<String> get7Slot(){
        List<String> list = new ArrayList<>();
        switch (gameAnsType){
            case 0:
                list.add("-100 to -72");
                list.add("-71 to -44");
                list.add("-43 to -16");
                list.add("-15 to 12");
                list.add("13 to 40");
                list.add("41 to 68");
                list.add("69 to 100");
                break;
            case 1:
               /* list.add("-10 to -8");
                list.add("-7 to -6");
                list.add("-5 to -4");
                list.add("-3 to -2");
                list.add("-1 to 0");
                list.add("1 to 2");
                list.add("3 to 10");*/
                list.add("-10 to -8");
                list.add("-7 to -4");
                list.add("-3 to -1");
                list.add("0 to 0");
                list.add("1 to 3");
                list.add("4 to 7");
                list.add("8 to 10");
                break;
            case 2:
                list.add("0 to 1");
                list.add("2 to 2");
                list.add("3 to 3");
                list.add("4 to 4");
                list.add("5 to 5");
                list.add("6 to 6");
                list.add("7 to 9");
                break;
        }
        return list;
    }

    public List<String> get8Slot(){
        List<String> list = new ArrayList<>();
        switch (gameAnsType){
            case 0:
                list.add("-100 to -75");
                list.add("-74 to -50");
                list.add("-49 to -25");
                list.add("-24 to 0");
                list.add("1 to 25");
                list.add("26 to 50");
                list.add("51 to 75");
                list.add("76 to 100");
                break;
            case 1:
                list.add("-10 to -8");
                list.add("-7 to -6");
                list.add("-5 to -4");
                list.add("-3 to -2");
                list.add("-1 to 0");
                list.add("1 to 2");
                list.add("3 to 4");
                list.add("5 to 10");
                break;
            case 2:
                list.add("0 to 1");
                list.add("2 to 2");
                list.add("3 to 3");
                list.add("4 to 4");
                list.add("5 to 5");
                list.add("6 to 6");
                list.add("7 to 7");
                list.add("8 to 9");
                break;
        }
        return list;
    }

    public List<String> get9Slot(){
        List<String> list = new ArrayList<>();
        switch (gameAnsType){
            case 0:
                list.add("-100 to -78");
                list.add("-77 to -56");
                list.add("-55 to -34");
                list.add("-33 to -12");
                list.add("-11 to 10");
                list.add("11 to 32");
                list.add("33 to 54");
                list.add("55 to 76");
                list.add("77 to 100");
                break;
            case 1:
                list.add("-10 to -8");
                list.add("-7 to -6");
                list.add("-5 to -4");
                list.add("-3 to -2");
                list.add("-1 to 0");
                list.add("1 to 2");
                list.add("3 to 4");
                list.add("5 to 6");
                list.add("7 to 10");
                break;
            case 2:
                list.add("0 to 1");
                list.add("2 to 2");
                list.add("3 to 3");
                list.add("4 to 4");
                list.add("5 to 5");
                list.add("6 to 6");
                list.add("7 to 7");
                list.add("8 to 8");
                list.add("9 to 9");
                break;
        }
        return list;
    }


    public List<String> get10Slot(){
        List<String> list = new ArrayList<>();
        switch (gameAnsType){
            case 0:
                list.add("-100 to -80");
                list.add("-79 to -60");
                list.add("-59 to -40");
                list.add("-39 to -20");
                list.add("-19 to 0");
                list.add("1 to 20");
                list.add("21 to 40");
                list.add("41 to 60");
                list.add("61 to 80");
                list.add("81 to 100");
                break;
            case 1:
                list.add("-10 to -8");
                list.add("-7 to -6");
                list.add("-5 to -4");
                list.add("-3 to -2");
                list.add("-1 to 0");
                list.add("1 to 2");
                list.add("3 to 4");
                list.add("5 to 6");
                list.add("7 to 8");
                list.add("9 to 10");
                break;
            case 2:
                list.add("0 to 0");
                list.add("1 to 1");
                list.add("2 to 2");
                list.add("3 to 3");
                list.add("4 to 4");
                list.add("5 to 5");
                list.add("6 to 6");
                list.add("7 to 7");
                list.add("8 to 8");
                list.add("9 to 9");
                break;
        }
        return list;
    }
}

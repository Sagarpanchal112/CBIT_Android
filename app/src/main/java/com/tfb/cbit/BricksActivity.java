/*
package com.tfb.cbit;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tfb.cbit.adapter.BricksAdapter;
import com.tfb.cbit.adapter.SeekBarAdapter;
import com.tfb.cbit.interfaces.OnRangeListener;
import com.tfb.cbit.models.contestdetails.BoxJson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BricksActivity extends AppCompatActivity {

    @BindView(R.id.rvBricks)
    RecyclerView rvBricks;
    @BindView(R.id.rvSeekbar)
    RecyclerView rvSeekbar;
    private Context context;
    int  colorArray[] ={R.color.color_green,R.color.color_red,R.color.color_blue};
    private ArrayList<Integer> bricksItems = new ArrayList<>();
   // private ArrayList<Integer> bricksColor = new ArrayList<>();
    private ArrayList<HashMap<String,Integer>> bricksColorModel = new ArrayList<>();
    private BricksAdapter bricksAdapter;
    private ArrayList<HashMap<String,Integer>> bricksModels = new ArrayList<>();
    private int column = 0;
    private Handler handler = new Handler();
    private boolean isHandlerPost = false;
    private List<BoxJson> boxJsonList ;
    private String boxJson = "[\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 1000\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"blue\",\n" +
            "    \"symbol\": \"+\",\n" +
            "    \"number\": 400\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 500\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"blue\",\n" +
            "    \"symbol\": \"+\",\n" +
            "    \"number\": 500\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"blue\",\n" +
            "    \"symbol\": \"+\",\n" +
            "    \"number\": 600\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 100\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"blue\",\n" +
            "    \"symbol\": \"+\",\n" +
            "    \"number\": 700\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 200\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 400\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"red\",\n" +
            "    \"symbol\": \" - \",\n" +
            "    \"number\": 950\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 300\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"red\",\n" +
            "    \"symbol\": \" - \",\n" +
            "    \"number\": 750\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"red\",\n" +
            "    \"symbol\": \" - \",\n" +
            "    \"number\": 650\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 500\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"red\",\n" +
            "    \"symbol\": \" - \",\n" +
            "    \"number\": 450\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 1000\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 700\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"blue\",\n" +
            "    \"symbol\": \"+\",\n" +
            "    \"number\": 800\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 900\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"blue\",\n" +
            "    \"symbol\": \"+\",\n" +
            "    \"number\": 900\n" +
            "  },\n" +
            "   {\n" +
            "    \"color\": \"blue\",\n" +
            "    \"symbol\": \"+\",\n" +
            "    \"number\": 100\n" +
            "  },\n" +
            "   {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 400\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"blue\",\n" +
            "    \"symbol\": \"+\",\n" +
            "    \"number\": 200\n" +
            "  },\n" +
            "   {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 200\n" +
            "  },\n" +
            "   {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 300\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"red\",\n" +
            "    \"symbol\": \" - \",\n" +
            "    \"number\": 850\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 500\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"red\",\n" +
            "    \"symbol\": \" - \",\n" +
            "    \"number\": 450\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"red\",\n" +
            "    \"symbol\": \" - \",\n" +
            "    \"number\": 550\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 600\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"red\",\n" +
            "    \"symbol\": \" - \",\n" +
            "    \"number\": 750\n" +
            "  },\n" +
            "  {\n" +
            "    \"color\": \"green\",\n" +
            "    \"symbol\": \"*\",\n" +
            "    \"number\": 800\n" +
            "  }\n" +
            "  \n" +
            "]";
    List<HashMap<String,String>> hashMapList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bricks);
        ButterKnife.bind(this);
        context = this;

        inItBricks();
        rvBricks.setLayoutManager(new GridLayoutManager(context,4));
        //Game Not Start
        bricksAdapter = new BricksAdapter(context,bricksItems,bricksColorModel);
        rvBricks.setAdapter(bricksAdapter);
        isHandlerPost = handler.post(runnable);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<BoxJson>>(){}.getType();
        boxJsonList = gson.fromJson(boxJson,listType);

       // Log.d("TAG",""+boxJsonList.size());

        HashMap<String,String> map = new HashMap<>();
        map.put("step","3");
        map.put("min","-10");
        map.put("max","10");
        map.put("updatestep","");
        hashMapList.add(map);
        map = new HashMap<>();
        map.put("step","5");
        map.put("min","-10");
        map.put("max","10");
        map.put("updatestep","");
        hashMapList.add(map);
        map = new HashMap<>();
        map.put("step","8");
        map.put("min","-10");
        map.put("max","10");
        map.put("updatestep","");
        hashMapList.add(map);

        rvSeekbar.setLayoutManager(new LinearLayoutManager(context));
        final SeekBarAdapter seekBarAdapter = new SeekBarAdapter(context,hashMapList);
        seekBarAdapter.setOnRangeListener(new OnRangeListener() {
            @Override
            public void onRangeValue(int minValue, int maxValue, final int position) {
                hashMapList.get(position).put("step",minValue+"");
                rvSeekbar.post(new Runnable() {
                    @Override
                    public void run() {
                        seekBarAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
        rvSeekbar.setAdapter(seekBarAdapter);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here
           // inItChange();
            inIt();
            // Repeat every 1 seconds
            handler.postDelayed(runnable, 1000);
        }
    };


    @OnClick(R.id.btnStop)
    protected void btnStopClick(){
        handler.removeCallbacks(runnable);
        setRealData();
    }

    private void setRealData(){
        for(int i=0;i<bricksColorModel.size();i++){
            HashMap<String,Integer> map = bricksColorModel.get(i);
            boxJsonList.set(i,boxJsonList.get(map.get("index")));
        }

        bricksAdapter = new BricksAdapter(context,boxJsonList,true);
        rvBricks.setAdapter(bricksAdapter);
    }

    private void inIt(){
        for(int i=0;i<4;i++){
            HashMap<String, Integer> temp = bricksColorModel.get(bricksColorModel.size()-1);
            bricksColorModel.remove(bricksColorModel.size()-1);
            bricksColorModel.add(0,temp);
        }

        bricksAdapter.notifyDataSetChanged();
    }


    private void inItBricks(){

        //Set Dummy Bricks Value
        int i=50;
        while (i<=1000){
            bricksItems.add(i);
            i = i +50;
        }

        for(int j = 0; j<12;j++){
            bricksItems.add(bricksItems.get(j));
        }

        Collections.shuffle(bricksItems);


        HashMap<String, Integer> map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",0);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_blue);
        map.put("index",1);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",2);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_blue);
        map.put("index",3);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_blue);
        map.put("index",4);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",5);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_blue);
        map.put("index",6);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",7);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",8);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_red);
        map.put("index",9);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",10);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_red);
        map.put("index",11);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_red);
        map.put("index",12);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",13);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_red);
        map.put("index",14);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",15);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",16);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_blue);
        map.put("index",17);
        bricksColorModel.add(map);


        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",18);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_blue);
        map.put("index",19);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_blue);
        map.put("index",20);
        bricksColorModel.add(map);


        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",21);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_blue);
        map.put("index",22);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",23);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",24);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_red);
        map.put("index",25);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",26);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_red);
        map.put("index",27);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_red);
        map.put("index",28);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",29);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_red);
        map.put("index",30);
        bricksColorModel.add(map);

        map = new HashMap<>();
        map.put("color",R.color.color_green);
        map.put("index",31);
        bricksColorModel.add(map);

        */
/*bricksColor.add(R.color.color_green);
        bricksColor.add(R.color.color_blue);
        bricksColor.add(R.color.color_green);
        bricksColor.add(R.color.color_blue);
        bricksColor.add(R.color.color_blue);
        bricksColor.add(R.color.color_green);
        bricksColor.add(R.color.color_blue);
        bricksColor.add(R.color.color_green);*//*


    }

}
*/

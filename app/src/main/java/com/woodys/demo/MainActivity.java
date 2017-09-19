package com.woodys.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.woodys.demo.eventcollect.mouble.ActionItem;
import com.woodys.demo.eventcollect.mouble.Type;
import com.woodys.eventcollect.EventCollectsManager;

import java.util.Random;

import quant.actionrecord.sample.eventcollect.EvnetsManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int token = new Random().nextInt(10000);
                Type type = null;

                switch (new Random().nextInt(4)+1){
                    case 1:
                        type = Type.ACTIVITY_OPEN;
                        break;
                    case 2:
                        type = Type.CLICK;
                        break;
                    case 3:
                        type = Type.LIST_CLICK;
                        break;
                    case 4:
                        type = Type.ACTIVITY_CLOSE;
                        break;
                    default:
                        type = Type.ACTIVITY_OPEN;
                        break;
                }
                String clazzName = type.name();
                String value = "测试："+String.valueOf(type.ordinal());
                EvnetsManager.INSTANCE.addAction(new ActionItem(token,type,clazzName,value,null));
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventCollectsManager.get().sendAction();
            }
        });

    }
}

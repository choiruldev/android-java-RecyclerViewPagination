package com.chrl.recyclerviewpagination;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //Initial variable
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<MainData> dataArrayList = new ArrayList<>();
    MainAdapter adapter;
    int page = 1, limit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        nestedScrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        //Initialize adapter
        adapter = new MainAdapter(MainActivity.this,dataArrayList);
        //Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Set adapter
        recyclerView.setAdapter(adapter);

        //Crete get data method
         getData(page,limit);

         nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
             @Override
             public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                 //check condition
                 if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                     //when reach last item position
                     //incress page size
                     page ++;
                     //show progress bar
                     progressBar.setVisibility(View.VISIBLE);
                     //call method
                     getData(page,limit);
                 }
             }
         });
    }

    private void getData(int page, int limit) {
        //Initialize retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://picsum.photos/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        //Create mamin interface
        MainInterface mainInterface = retrofit.create(MainInterface.class);
        //Initialize call
        Call<String> call = mainInterface.STRING_CALL(page,limit);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Check validation
                if(response.isSuccessful()&& response.body() !=null){
                    //hide progressbar
                    progressBar.setVisibility(View.GONE);
                    try {
                        //Initilize json array
                        JSONArray jsonArray = new JSONArray(response.body());
                        Log.i(TAG, "onResponse: "+response.body());
                        //Parse json array
                        parseResult(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void parseResult(JSONArray jsonArray) {
        //Use for loop
        for(int i=0; i<jsonArray.length(); i++){
            //Initialize json object
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                //Initlize main data
                MainData data = new MainData();
                //set image
                data.setImage(object.getString("download_url"));
                //set name
                data.setName(object.getString("author"));
                //add data in array
                dataArrayList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //initialize adapter
            adapter = new MainAdapter(this,dataArrayList);
            //set adapter
            recyclerView.setAdapter(adapter);
        }
    }

}
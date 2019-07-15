package com.devcro.randomchooserseminarskirad;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, MyRecyclerViewAdapterAlert.ItemClickListener {

    private MyRecyclerViewAdapter adapter;
    private MyRecyclerViewAdapterAlert adapterAlert;
    private ArrayList<String> savedJsons;
    private Button randBtn;
    private ArrayList<String> itemList;
    private EditText opt;
    private ImageView slika;
    private TextView result;
    private String saveListName = "";
    private String path = "";
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final Button addBtn = (Button) findViewById(R.id.addBtn);
        randBtn = (Button) findViewById(R.id.randBtn);
        //final Button clrBtn = (Button)findViewById(R.id.clearBtn);
        result = (TextView) findViewById(R.id.result);
        opt = (EditText) findViewById(R.id.opt);
        slika = (ImageView) findViewById(R.id.slika);
        itemList = new ArrayList<>();
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        path = getApplicationContext().getFilesDir().getAbsolutePath();


        // set up the RecyclerView
        final RecyclerView trenutno = findViewById(R.id.trenutno);
        trenutno.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, itemList);
        adapter.setClickListener(this);
        trenutno.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(trenutno.getContext(),
                layout.getOrientation());
        trenutno.addItemDecoration(dividerItemDecoration);


        slika.setVisibility(View.INVISIBLE);

        addBtn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        String upisano = opt.getText().toString();

                        if (TextUtils.isEmpty(upisano.trim())) {
                            Toast.makeText(MainActivity.this, "Blank!", Toast.LENGTH_SHORT).show();
                        } else {
                            itemList.add(upisano);
                            opt.setText("");
                            adapter.notifyDataSetChanged();
                        }
                    }

                }

        );


        randBtn.setOnClickListener(
                new Button.OnClickListener() {

                    public void onClick(View v) {

                        final int optSsize = itemList.size();

                        if (optSsize > 0) {

                            randBtn.setText("Randomizing");

                            Random r = new Random();
                            int rInt1 = r.nextInt(optSsize);
                            final String resS = itemList.get(rInt1);
                            slika.setVisibility(View.INVISIBLE);


                            new CountDownTimer(2500, 80) {


                                public void onTick(long millisUntilFinished) {
                                    randBtn.setClickable(false);
                                    addBtn.setClickable(false);

                                    Random r = new Random();
                                    int rInt = r.nextInt(optSsize);
                                    final String resSS = itemList.get(rInt);
                                    result.setText(resSS);
                                }

                                public void onFinish() {
                                    result.setText(resS);
                                    slika.setVisibility(View.VISIBLE);
                                    randBtn.setText("Randomize");
                                    randBtn.setClickable(true);
                                    addBtn.setClickable(true);

                                }
                            }.start();


                        } else {
                            Toast.makeText(MainActivity.this, "Add item!", Toast.LENGTH_SHORT).show();
                        }

                    }


                }
        );


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (randBtn.getText() == "Randomizing") {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.clearList:
                itemList.clear();
                adapter.notifyDataSetChanged();
                opt.setText("");
                result.setText("");
                slika.setVisibility(View.INVISIBLE);
                break;
            case R.id.saveList:

                if (itemList.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Enter list name:");

                    final EditText input = new EditText(this);

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveListName = input.getText().toString();
                            if(!TextUtils.isEmpty(saveListName.trim())) {
                                String jsonData = new Gson().toJson(itemList);

                                Writer output = null;
                                File file = new File(path + "/" + saveListName + ".json");
                                try {
                                    output = new BufferedWriter(new FileWriter(file));
                                    output.write(jsonData);
                                    output.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(MainActivity.this,  "List '"+saveListName+"' saved!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "List name cant be empty!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }
                else{
                    Toast.makeText(MainActivity.this, "No items to save!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.loadList:

                savedJsons = new ArrayList<>();

                File directory = new File(path);
                final File[] files = directory.listFiles();
                for (int i = 0; i < files.length; i++) {
                    savedJsons.add(files[i].getName().substring(0, files[i].getName().length() - 5));
                }

                if(savedJsons.size() > 0) {
                    AlertDialog.Builder builderDialog = new AlertDialog.Builder(MainActivity.this);
                    builderDialog.setTitle("Choose an list:");

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = (View) inflater.inflate(R.layout.recycleview_alert_dialog, null);

                    builderDialog.setView(dialogView);

                    RecyclerView rv = (RecyclerView) dialogView.findViewById(R.id.rv);
                    rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    adapterAlert = new MyRecyclerViewAdapterAlert(MainActivity.this, savedJsons);
                    adapterAlert.setClickListener(MainActivity.this);
                    rv.setAdapter(adapterAlert);


                    dialog = builderDialog.create();
                    dialog.show();
                }
                else{
                    Toast.makeText(MainActivity.this,  "No saved lists found!", Toast.LENGTH_SHORT).show();
                }

                break;


        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onItemClick(View view, int position) {
        if (randBtn.getText() != "Randomizing") {
            String kliknuto = String.valueOf(adapter.getItem(position));
            Toast.makeText(MainActivity.this, kliknuto + " deleted!", Toast.LENGTH_SHORT).show();

            itemList.remove(position);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClickDelete(View view, int position) {
        String kliknuto = String.valueOf(adapterAlert.getItem(position));

        File directory = new File(path);
        final File[] files = directory.listFiles();
        files[position].delete();
        savedJsons.remove(position);
        Toast.makeText(MainActivity.this, kliknuto + " deleted!", Toast.LENGTH_SHORT).show();
        adapterAlert.notifyDataSetChanged();
        if(savedJsons.size()<1){
            dialog.cancel();
        }
    }

    public void onItemClickLoad(View view, int position) {
        String kliknuto = String.valueOf(adapterAlert.getItem(position));
        Toast.makeText(MainActivity.this, "List '"+kliknuto + "' loaded!", Toast.LENGTH_SHORT).show();

        File file = new File(path + "/" + kliknuto + ".json");
        int length = (int) file.length();

        byte[] bytes = new byte[length];

        try {
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String contents = new String(bytes);
        Gson gson = new Gson();
        ArrayList<String> loadedItems = (ArrayList<String>) gson.fromJson(contents, ArrayList.class);
        itemList.clear();
        for (String loadedItem : loadedItems) {
            itemList.add(loadedItem);
        }
        adapter.notifyDataSetChanged();
        opt.setText("");
        result.setText("");
        slika.setVisibility(View.INVISIBLE);
        dialog.cancel();

    }


}




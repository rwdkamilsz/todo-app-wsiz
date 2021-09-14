package com.example.TodoAPP;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ViewSwitcher;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.TodoAPP.Adapters.ToDoAdapter;
import com.example.TodoAPP.Model.TaskModel;
import com.example.TodoAPP.Utils.DatabaseHandler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;

    private List<TaskModel> taskModelList;
    private Object ViewSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db,MainActivity.this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = findViewById(R.id.fab);
        taskModelList = db.getAllTasks();
        Collections.reverse(taskModelList);

        tasksAdapter.setTasks(taskModelList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        showItems(taskModelList);

    }
    public void showItems(List<TaskModel> items) {
        ViewSwitcher mListSwitcher = findViewById(R.id.switcher);
        if (items.size() > 0) {

            if (R.id.tasksRecyclerView == mListSwitcher.getNextView().getId()) {
                mListSwitcher.showNext();
            }
        } else if(R.id.text_empty == mListSwitcher.getNextView().getId()) {
            mListSwitcher.showNext();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.topmenu, menu);
        return true;
    }
    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskModelList = db.getAllTasks();
        Collections.reverse(taskModelList);
        tasksAdapter.setTasks(taskModelList);
        tasksAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAllButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(tasksAdapter.getContext());
                builder.setTitle(R.string.deleteAllTask);
                builder.setMessage(R.string.deleteAllTaskDescription).setPositiveButton(R.string.approve,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tasksAdapter.deleteAllItems();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tasksAdapter.notifyDataSetChanged();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
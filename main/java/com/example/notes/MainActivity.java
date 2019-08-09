package com.example.notes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
public class MainActivity extends AppCompatActivity  implements TimePickerDialog.OnTimeSetListener{
    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate( R.menu.add_menu,menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

      super.onOptionsItemSelected( item );
        int id = item.getItemId();
      if(id==R.id.add_note){
          Intent intent=new Intent( getApplicationContext(),NoteEditorActivity.class );
          startActivity( intent );

          return true;
      }



        if (id == R.id.action_cancel_alarm) {
            cancelAlarm();
            Toast.makeText(this, "Your alarm is canceled", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.action_set_alarm) {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");

            return true;

    }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        ListView listView=(ListView)findViewById( R.id.listView);
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.notes" , Context.MODE_PRIVATE);
        HashSet<String> set=(HashSet<String>)sharedPreferences.getStringSet( "notes",null );
        if(set==null) {//if its the first time this app runs
            notes.add( "example note" );
        }else{
            notes=new ArrayList( set );//if set has data then we load it
        }
        arrayAdapter=new ArrayAdapter( this,android.R.layout.simple_expandable_list_item_1,notes );
        listView.setAdapter( arrayAdapter );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent( getApplicationContext(),NoteEditorActivity.class );
                intent.putExtra( "noteID",position );
                startActivity( intent );
            }
        } );
        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final int itemToDelete=position;

                new AlertDialog.Builder( MainActivity.this ).setIcon(
                        android.R.drawable.ic_dialog_alert).setTitle( "are you sure?" )
                        .setMessage( "do you want to delete this note?" )
                        .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notes.remove( itemToDelete );
                                arrayAdapter.notifyDataSetChanged();

                                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.notes" , Context.MODE_PRIVATE);
                                HashSet<String> set=new HashSet(MainActivity.notes  );
                                sharedPreferences.edit().putStringSet( "notes",set ).apply();
                            }
                        } ).setNegativeButton( "No",null )
                        .show();


                return true;
            }
        } );
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();//after setting values in the time picker fragment we get the values here
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        startAlarm(c);
    }

    private void updateTimeText(Calendar c) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        Toast.makeText(this, timeText, Toast.LENGTH_LONG).show();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        //get the alarm service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        //pending intent so it can work even if we close the app
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        //set exact will fire at the c time and set the pending intent which contains alertReciver class
        //which call on receive method
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);//cancel the pending intent

    }


}


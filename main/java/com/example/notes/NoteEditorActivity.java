package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Calendar;
public class NoteEditorActivity extends AppCompatActivity {
    int noteId;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate( R.menu.share_menu,menu );


        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected( item );
        int id = item.getItemId();
        if(id==R.id.share_note){
            onShareNote();
            return true;
        }

        return false;
    }
    private void onShareNote() {
        String messageText=( MainActivity.notes.get( noteId ) );
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");

        share.putExtra(Intent.EXTRA_TEXT,  messageText);
        startActivity(share);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_note_editor );

        EditText editText=(EditText)findViewById( R.id.editText );
        final TextView textView=(TextView)findViewById( R.id.textView );
        Date currentTime = Calendar.getInstance().getTime();
        textView.setText( String.valueOf( currentTime ));
        Intent intent=getIntent();//get the intent that was used to get to this activity
        noteId=intent.getIntExtra("noteID",-1);
        if(noteId!=-1){
            editText.setText( MainActivity.notes.get( noteId ) );
        }else {//if its add new note

            MainActivity.notes.add( "" );
            noteId=MainActivity.notes.size()-1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }
        editText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                   MainActivity.notes.set( noteId,String.valueOf( s ) );
                   MainActivity.arrayAdapter.notifyDataSetChanged();
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.notes" , Context.MODE_PRIVATE);
                HashSet<String> set=new HashSet(MainActivity.notes  );
                sharedPreferences.edit().putStringSet( "notes",set ).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
    }
}

package com.thingsenz.soundy.videostreaming;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.ETC1;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thingsenz.soundy.R;

public class VSActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle saved) {

        super.onCreate(saved);
        setContentView(R.layout.activity_empty);
        showDialogPrompt();

    }

    private void showDialogPrompt() {

        LayoutInflater inflater=LayoutInflater.from(this);
        View promptsView=inflater.inflate(R.layout.layout_videostream_dialog,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(promptsView);
        final EditText userInputURL=(EditText)promptsView.findViewById(R.id.editTextDialogUrlInput);
        builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean isURL = Patterns.WEB_URL.matcher(userInputURL.getText().toString().trim()).matches();
                if (isURL) {
                    Intent mIntent = ExoPlayerActivity.getStartIntent(VSActivity.this, userInputURL.getText().toString().trim());
                    startActivity(mIntent);
                } else {
                    Toast.makeText(VSActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).create().show();

    }

}

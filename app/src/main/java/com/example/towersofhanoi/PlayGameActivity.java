package com.example.towersofhanoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PlayGameActivity extends AppCompatActivity {

    private EditText editTextNumDisks;
    private Button btnStartGame, btnGoHome;
    private TextView textErrorMessage;

    private void cancelToasts() {
        for (Toast toast : toasts) {
            toast.cancel();
        }
        toasts.clear();
    }

    private ArrayList<Toast> toasts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        editTextNumDisks = findViewById(R.id.editTextNumDisks);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnGoHome = findViewById(R.id.btnGoHome);

        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayGameActivity.this, MainActivity.class);
                cancelToasts();
                startActivity(intent);
            }
        });

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelToasts();
                startGame();
            }
        });
    }

    private void startGame() {
        // Get the number of disks entered by the user

        String numDisksStr = editTextNumDisks.getText().toString();
        if(numDisksStr.isEmpty()){
            Toast toast = new Toast(this);
            toast.setText("Please enter number of disks");
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 500);
            toasts.add(toast);
            toast.show();
            return;
        }
        int numDisks = Integer.parseInt(numDisksStr);

        // Validate the input (ensure it's at least 3)
        if (numDisks >= 3 && numDisks <= 10) {
             // Start the Tower of Hanoi game with the specified number of disks
             Intent intent = new Intent(this, TowerOfHanoiActivity.class);
             intent.putExtra("numDisks", numDisks);
             startActivity(intent);
        } else {
            // Display a Toast message indicating that at least 3 disks are required
            View layout = getLayoutInflater().inflate(R.layout.min_disk_layout, null);

            // Find the TextView within the custom layout
            TextView textMessage = layout.findViewById(R.id.textMessage);

            // Set the text of the TextView
            textMessage.setText("Minimum 3 and maximum 10 disks to play");

            // Create and show the toast message with the custom layout
            Toast toast2 = new Toast(this);
            toast2.setDuration(Toast.LENGTH_SHORT);
            toast2.setView(layout);
            toast2.setGravity(Gravity.CENTER, 0, -300);
            toasts.add(toast2);
            toast2.show();

        }
    }
}

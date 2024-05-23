package com.example.towersofhanoi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Stack;

public class TowerOfHanoiActivity extends AppCompatActivity {

    private LinearLayout tower1, tower2, tower3;
    private Stack<TextView> tower1Stack, tower2Stack, tower3Stack;
    private int numDisks;
    private int movesCount = 0;
    private boolean isPaused = false;
    private boolean isGameOver = false;

    private void cancelToasts() {
        for (Toast toast : toasts) {
            toast.cancel();
        }
        toasts.clear();
    }

    private ArrayList<Toast> toasts = new ArrayList<>();

    @Override
    protected void onPause() {
        super.onPause();
        if (isPaused) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPaused) {
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }

    private Button btnGoHome, btnViewSolution;
    private TextView timerTextView;
    private TextView moveCounterTextView;
    private long startTime = 0;
    private Drawable originalBackground;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("Time: %02d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    private Stack<TextView> selectedStack = null;
    private TextView selectedDisk = null;

    private ArrayList<String> colors = new ArrayList<>(Arrays.asList("red", "green", "blue",
            "yellow", "cyan", "magenta", "burgundy", "olive", "beige", "purple"));

    private DBHelper dbHelper;
    private LevelDataSource levelDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tower_of_hanoi);

        Collections.shuffle(colors);

        timerTextView = findViewById(R.id.timerTextView);
        moveCounterTextView = findViewById(R.id.moveCounterTextView);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        btnGoHome = findViewById(R.id.btnGoHome);
        btnViewSolution = findViewById(R.id.btnViewSolution);

        dbHelper = new DBHelper(this);
        levelDataSource = new LevelDataSource(this);
        levelDataSource.open();

        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TowerOfHanoiActivity.this, MainActivity.class);
                cancelToasts();
                startActivity(intent);
            }
        });

        btnViewSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TowerOfHanoiActivity.this, DisplaySolutionActivity.class);
                intent.putExtra("numDisks", numDisks);
                cancelToasts();
                startActivity(intent);
                isPaused = true;
            }
        });

        // Initialize towers
        tower1 = findViewById(R.id.tower1);
        tower2 = findViewById(R.id.tower2);
        tower3 = findViewById(R.id.tower3);

        // Initialize stacks for disks on each tower
        tower1Stack = new Stack<>();
        tower2Stack = new Stack<>();
        tower3Stack = new Stack<>();

        // Get the number of disks passed from the previous activity
        numDisks = getIntent().getIntExtra("numDisks", 3);

        // Generate disks and add them to tower 1
        generateDisks(tower1Stack, tower1);

        // Set click listeners for towers
        tower1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDisk(tower1Stack, tower1);
            }
        });

        tower2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDisk(tower2Stack, tower2);
            }
        });

        tower3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDisk(tower3Stack, tower3);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        levelDataSource.close();
    }

    private void generateDisks(Stack<TextView> towerStack, LinearLayout towerLayout) {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        float diskHeight = (float) (0.6 * screenHeight / numDisks);
        float initialDiskWidth = (float) (0.25 * screenWidth);

        for (int i = numDisks; i >= 1; i--) {
            TextView disk = new TextView(this);
            disk.setText(String.valueOf(i));
            disk.setTextSize(24);
            float diskWidth = initialDiskWidth;
            initialDiskWidth -= 0.2 * initialDiskWidth;

            // Create a bitmap for the disk image
            Bitmap diskBitmap = createDiskBitmap(i);

            // Convert the bitmap to a drawable and set it as the background
            disk.setBackground(new BitmapDrawable(getResources(), diskBitmap));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) diskWidth, (int) diskHeight);
            params.setMargins(0, 0, 0, 0);
            disk.setLayoutParams(params);

            // Center text inside the disk
            disk.setGravity(Gravity.CENTER);
            towerStack.push(disk);
        }
        Stack<TextView> tempStack = (Stack<TextView>)towerStack.clone();

        for (int i = 0; i < numDisks; i++) {
            towerLayout.addView(tempStack.pop());
        }
    }

    private Bitmap createDiskBitmap(int size) {
        // Create a bitmap with the desired size
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        // Generate a random color for the disk
        int colorIdx = (int) (Math.random() * colors.size());
        String diskColor = colors.get(colorIdx);
        int color = getResources().getIdentifier(diskColor, "color", getPackageName());
        colors.remove(colorIdx);
        paint.setColor(getResources().getColor(color, null));

        // Draw the disk shape
        canvas.drawRect(0, 0, 100, 100, paint);

        return bitmap;
    }

    private void moveDisk(Stack<TextView> stack, LinearLayout tower) {
        if (isGameOver) return; // Prevent any move if the game is over

        if (selectedDisk == null) {
            // No disk selected, select the top disk from the clicked tower
            if (!stack.isEmpty()) {
                selectedDisk = stack.pop();
                selectedStack = stack;
                originalBackground = selectedDisk.getBackground();
                selectedDisk.setBackgroundColor(Color.YELLOW); // Highlight the selected disk
            }
        } else {
            // Disk already selected, move it to the clicked tower
            if (isValidMove(stack, selectedDisk)) {
                selectedDisk.setBackground(originalBackground); // Remove highlight
                ((LinearLayout) selectedDisk.getParent()).removeView(selectedDisk);
                tower.addView(selectedDisk, 0);
                stack.push(selectedDisk);
                selectedDisk = null;
                selectedStack = null;
                movesCount++;
                moveCounterTextView.setText("Moves: " + movesCount);
                checkForWin();
            } else {
                // Invalid move, return the disk to its original tower
                selectedStack.push(selectedDisk);
                selectedDisk.setBackground(originalBackground); // Remove highlight
                selectedDisk = null;
                selectedStack = null;
                Toast toastMove = Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT);
                toasts.add(toastMove);
                toastMove.show();
            }
        }
    }

    private boolean isValidMove(Stack<TextView> toStack, TextView disk) {
        if (toStack.isEmpty()) {
            return true;
        } else {
            TextView topDisk = toStack.peek();
            int diskSize = Integer.parseInt(disk.getText().toString());
            int topDiskSize = Integer.parseInt(topDisk.getText().toString());
            return diskSize < topDiskSize;
        }
    }

    private void checkForWin() {
        if (tower3Stack.size() == numDisks) {
            isGameOver = true; // Set the game over flag
            timerHandler.removeCallbacks(timerRunnable); // Stop the timer
            timerTextView.setTextColor(Color.GREEN);
            timerTextView.setText(timerTextView.getText() + "\nCongratulations! You won!");
            moveCounterTextView.setTextColor(Color.GREEN);
            Toast toastWin = Toast.makeText(this, "You won in " + movesCount + " moves!", Toast.LENGTH_LONG);
            toasts.add(toastWin);
            toastWin.show();

            // Extract minutes and seconds from the timer text
            String[] timeParts = timerTextView.getText().toString().split(":");
            String minutes = timeParts[1].substring(1, 3);
            String seconds = timeParts[2].substring(0, 2);
            String bestTime = minutes + ":" + seconds;

            // Update the database with the best time and lowest moves
            dbHelper.updateLevel(numDisks, bestTime, movesCount);
        }
    }
}

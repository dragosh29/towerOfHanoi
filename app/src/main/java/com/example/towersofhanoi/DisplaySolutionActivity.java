package com.example.towersofhanoi;

import android.content.Context;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


public class DisplaySolutionActivity extends AppCompatActivity {

    private LinearLayout tower1, tower2, tower3;
    private Drawable originalBackground;

    private ArrayList<String> colors = new ArrayList<>(Arrays.asList("red", "green", "blue",
            "yellow", "cyan", "magenta", "burgundy", "olive", "beige", "purple"));

    private ArrayList<Toast> toasts = new ArrayList<>();

    private Stack<TextView> tower1Stack, tower2Stack, tower3Stack;
    private int numDisks;
    private int movesCount = 0;
    private TextView moveCounterTextView;

    private TowerOfHanoiSolver solver;
    private List<TowerOfHanoiSolver.Move> solutionSteps;
    private int currentStep = 0;
    private Button btnGoBack;
    private Handler solutionHandler = new Handler();

    private void cancelToasts() {
        System.out.println("Cancelling toasts " + toasts);
        for (Toast toast : toasts) {
            toast.cancel();
        }
        toasts.clear();
        System.out.println("Toasts cleared " + toasts);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_solution);

        Collections.shuffle(colors);

        moveCounterTextView = findViewById(R.id.moveCounterTextView);
        btnGoBack = findViewById(R.id.btnGoBack);

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solutionHandler.removeCallbacks(solutionRunnable);
                cancelToasts();
                finish();
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

        // Get the number of disks from the previous activity
        numDisks = getIntent().getIntExtra("numDisks", 3);

        // Generate disks and add them to tower 1
        generateDisks(tower1Stack, tower1);

        solver = new TowerOfHanoiSolver();
        solutionSteps = solver.getSolutionSteps(numDisks);

        startSolution();
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
        System.out.println("Disks generated");
    }

    private Bitmap createDiskBitmap(int size) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        int colorIdx = (int) (Math.random() * colors.size());
        String diskColor = colors.get(colorIdx);
        int color = getResources().getIdentifier(diskColor, "color", getPackageName());
        colors.remove(colorIdx);
        paint.setColor(getResources().getColor(color, null));

        canvas.drawRect(0, 0, 100, 100, paint);

        return bitmap;
    }

    private void startSolution() {
        currentStep = 0;
        solutionHandler.postDelayed(solutionRunnable, 1000);
    }

    private Runnable solutionRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentStep < solutionSteps.size()) {
                TowerOfHanoiSolver.Move move = solutionSteps.get(currentStep);
                performMove(move);
                currentStep++;
                solutionHandler.postDelayed(this, 1500);
            } else {
                moveCounterTextView.setText("Completed in\n" + movesCount + " moves!");
                moveCounterTextView.setTextColor(Color.GREEN);
                cancelToasts();
                Toast toast = Toast.makeText(DisplaySolutionActivity.this, "Solution completed!", Toast.LENGTH_LONG);
                toasts.add(toast);
                toast.show();
            }
        }
    };

    private void performMove(TowerOfHanoiSolver.Move move) {
        Stack<TextView> fromStack = getStackFromRod(move.fromRod);
        Stack<TextView> toStack = getStackFromRod(move.toRod);
        LinearLayout toLayout = getLayoutFromRod(move.toRod);

        if (fromStack != null && toStack != null && toLayout != null && !fromStack.isEmpty()) {
            TextView disk = fromStack.pop();
            originalBackground = disk.getBackground();
            disk.setBackgroundColor(Color.YELLOW);
            ((LinearLayout) disk.getParent()).removeView(disk);
            disk.setBackground(originalBackground);
            toLayout.addView(disk, 0);
            toStack.push(disk);
            movesCount++;
            moveCounterTextView.setText("Moves: " + movesCount);
        }
    }

    private Stack<TextView> getStackFromRod(char rod) {
        switch (rod) {
            case 'A': return tower1Stack;
            case 'B': return tower2Stack;
            case 'C': return tower3Stack;
            default: return null;
        }
    }

    private LinearLayout getLayoutFromRod(char rod) {
        switch (rod) {
            case 'A': return tower1;
            case 'B': return tower2;
            case 'C': return tower3;
            default: return null;
        }
    }
}

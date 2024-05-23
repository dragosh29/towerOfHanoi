package com.example.towersofhanoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class RecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecords;
    private TextView textViewEmpty;
    private Button btnGoHome;
    private LevelDataSource levelDataSource;
    private List<LevelEntry> levelEntries;
    private RecordsAdapter recordsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        recyclerViewRecords = findViewById(R.id.recyclerViewRecords);
        recyclerViewRecords.setLayoutManager(new LinearLayoutManager(this));

        textViewEmpty = findViewById(R.id.textViewEmpty);
        btnGoHome = findViewById(R.id.btnGoHome);

        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the home activity
                Intent intent = new Intent(RecordsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        levelDataSource = new LevelDataSource(this);
        levelDataSource.open();

        // Fetch level entries from the database
        levelEntries = levelDataSource.getAllLevelEntries();

        // Sort level entries by disk number
        Collections.sort(levelEntries);

        // Set visibility of empty view
        if (levelEntries.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
        }

        // Set up RecyclerView adapter
        recordsAdapter = new RecordsAdapter(levelEntries);
        recyclerViewRecords.setAdapter(recordsAdapter);
    }

    @Override
    protected void onDestroy() {
        levelDataSource.close(); // Close the database
        super.onDestroy();
    }

    // ViewHolder class for RecyclerView
    private static class RecordsViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewLevel;
        private TextView textViewBestTime;
        private TextView textViewLowestMoves;
        private TextView textViewDateSet;
        private Button btnDelete;

        RecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLevel = itemView.findViewById(R.id.textViewLevel);
            textViewBestTime = itemView.findViewById(R.id.textViewBestTime);
            textViewLowestMoves = itemView.findViewById(R.id.textViewLowestMoves);
            textViewDateSet = itemView.findViewById(R.id.textViewDateSet);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(LevelEntry levelEntry, LevelDataSource levelDataSource, RecordsAdapter adapter) {
            textViewLevel.setText("Level: " + levelEntry.getLevel());
            textViewBestTime.setText("Best Time: " + levelEntry.getBestTime());
            textViewLowestMoves.setText("Lowest Moves: " + levelEntry.getLowestMoves());
            textViewDateSet.setText("Date Set: " + levelEntry.getDateSet());

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    levelDataSource.deleteLevelEntry(levelEntry.getLevel());
                    adapter.removeEntry(getAdapterPosition());
                }
            });
        }
    }

    // RecyclerView adapter
    private class RecordsAdapter extends RecyclerView.Adapter<RecordsViewHolder> {
        private List<LevelEntry> levelEntries;

        RecordsAdapter(List<LevelEntry> levelEntries) {
            this.levelEntries = levelEntries;
        }

        @NonNull
        @Override
        public RecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_record, parent, false);
            return new RecordsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordsViewHolder holder, int position) {
            LevelEntry levelEntry = levelEntries.get(position);
            holder.bind(levelEntry, levelDataSource, this);
        }

        @Override
        public int getItemCount() {
            return levelEntries.size();
        }

        void removeEntry(int position) {
            levelEntries.remove(position);
            notifyItemRemoved(position);

            if (levelEntries.isEmpty()) {
                textViewEmpty.setVisibility(View.VISIBLE);
            }
        }
    }
}

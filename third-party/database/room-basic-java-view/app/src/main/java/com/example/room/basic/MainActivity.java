package com.example.room.basic;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etAge;
    private Button btnAdd;
    private Button btnUpdate;
    private Button btnDeleteAll;
    private RecyclerView rvUsers;

    private UserViewModel userViewModel;
    private UserAdapter adapter;
    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initViewModel();
        setupRecyclerView();
        setupListeners();
        observeUsers();
        initButtonStates();
    }

    private void initButtonStates() {
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        rvUsers = findViewById(R.id.rvUsers);
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                selectedUser = user;
                etName.setText(user.getName());
                etEmail.setText(user.getEmail());
                etAge.setText(String.valueOf(user.getAge()));
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(true);
            }

            @Override
            public void onDeleteClick(User user) {
                userViewModel.delete(user);
                Toast.makeText(MainActivity.this, R.string.toast_delete_success, Toast.LENGTH_SHORT).show();
                clearInput();
            }
        });

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);
    }

    private void setupListeners() {
        btnAdd.setOnClickListener(v -> addUser());
        btnUpdate.setOnClickListener(v -> updateUser());
        btnDeleteAll.setOnClickListener(v -> deleteAllUsers());
    }

    private void observeUsers() {
        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapter.submitList(users);
            }
        });
    }

    private void addUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(ageStr)) {
            Toast.makeText(this, R.string.toast_input_error, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            User user = new User(name, email, age);
            userViewModel.insert(user);
            Toast.makeText(this, R.string.toast_add_success, Toast.LENGTH_SHORT).show();
            clearInput();
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.toast_input_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser() {
        if (selectedUser == null) {
            Toast.makeText(this, R.string.toast_select_user, Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(ageStr)) {
            Toast.makeText(this, R.string.toast_input_error, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            selectedUser.setName(name);
            selectedUser.setEmail(email);
            selectedUser.setAge(age);
            userViewModel.update(selectedUser);
            Toast.makeText(this, R.string.toast_update_success, Toast.LENGTH_SHORT).show();
            clearInput();
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.toast_input_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAllUsers() {
        userViewModel.deleteAllUsers();
        Toast.makeText(this, R.string.toast_delete_all_success, Toast.LENGTH_SHORT).show();
        clearInput();
    }

    private void clearInput() {
        etName.setText("");
        etEmail.setText("");
        etAge.setText("");
        selectedUser = null;
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
    }
}
package com.example.database.basic;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.util.List;

/**
 * 主界面
 * <p>
 * 注意，界面逻辑主要符合版本为 1 的数据库
 */
public class MainActivity extends AppCompatActivity {
    private EditText etName, etAge;
    private Button btnAdd, btnUpdate, btnDelete;
    private ListView lvStudents;
    private TextView tvEmpty;
    private StudentDao studentDao;
    private List<Student> studentList;
    private ArrayAdapter<Student> adapter;
    private int selectedStudentId = -1;

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
        initData();
        setListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        lvStudents = findViewById(R.id.lvStudents);
        tvEmpty = findViewById(R.id.tvEmpty);

        studentDao = new StudentDao(this);
    }

    private void initData() {
        // 设置适配器
        adapter = new ArrayAdapter<Student>(this, R.layout.item_student, R.id.tvName) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Student student = getItem(position);

                TextView tvId = view.findViewById(R.id.tvId);
                TextView tvName = view.findViewById(R.id.tvName);
                TextView tvAge = view.findViewById(R.id.tvAge);

                tvId.setText(String.valueOf(student.getId()));
                tvName.setText(student.getName());
                tvAge.setText(student.getAge() + "岁");

                return view;
            }
        };

        lvStudents.setAdapter(adapter);
        lvStudents.setEmptyView(tvEmpty);

        refreshData();
    }

    private void setListeners() {
        // 添加按钮点击事件
        btnAdd.setOnClickListener(v -> addStudent());

        // 更新按钮点击事件
        btnUpdate.setOnClickListener(v -> updateStudent());

        // 删除按钮点击事件
        btnDelete.setOnClickListener(v -> deleteStudent());

        // 列表项点击事件
        lvStudents.setOnItemClickListener((parent, view, position, id) -> {
            Student student = studentList.get(position);
            selectedStudentId = student.getId();
            etName.setText(student.getName());
            etAge.setText(String.valueOf(student.getAge()));
        });
    }

    private void addStudent() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        Student student = new Student(name, age);
        long result = studentDao.insert(student);

        if (result > 0) {
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            clearInput();
            refreshData();
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStudent() {
        if (selectedStudentId == -1) {
            Toast.makeText(this, "请先选择要修改的学生", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        Student student = new Student(name, age);
        student.setId(selectedStudentId);

        int result = studentDao.update(student);

        if (result > 0) {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            clearInput();
            refreshData();
            selectedStudentId = -1;
        } else {
            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteStudent() {
        if (selectedStudentId == -1) {
            Toast.makeText(this, "请先选择要删除的学生", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = studentDao.delete(selectedStudentId);

        if (result > 0) {
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            clearInput();
            refreshData();
            selectedStudentId = -1;
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 刷新学生数据列表
     * 从数据库中查询所有学生信息，并更新适配器中的数据
     */
    private void refreshData() {
        studentList = studentDao.queryAll();
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(studentList);
            adapter.notifyDataSetChanged();
        }
    }

    private void clearInput() {
        etName.setText("");
        etAge.setText("");
    }
}
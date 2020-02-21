package com.example.firebasesocialapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    //view
    TextInputEditText tiEdtEmail, tiEdtPassword;
    Button btnSignUp;
    TextView tvHaveAcct;

    //progress dialog
    ProgressDialog progressDialog;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Action bar and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Đăng ký");
        //endable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init
        tiEdtEmail = findViewById(R.id.tiEdtEmail);
        tiEdtPassword = findViewById(R.id.tiEdtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvHaveAcct = findViewById(R.id.tvHaveAcct);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đăng ký...");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //inputs
                String email = tiEdtEmail.getText().toString().trim();
                String password = tiEdtPassword.getText().toString().trim();
                //validations
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    tiEdtEmail.setError("Email không hợp lệ!");
                    tiEdtEmail.requestFocus();
                } else if (password.length() < 6) {
                    tiEdtPassword.setError("Mật khẩu phải có tối thiểu 6 kí tự!");
                    tiEdtPassword.requestFocus();
                } else {
                    registerUser(email, password);
                }
            }
        });

        tvHaveAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });
    }

    private void registerUser(String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, move to ProfileActivity
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công với email: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, ProfileActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, "Lỗi: " + e, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

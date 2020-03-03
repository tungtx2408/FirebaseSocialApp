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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    //view
    TextInputEditText tiEdtEmail, tiEdtPassword;
    TextInputLayout tilEmail, tilPassword;
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
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvHaveAcct = findViewById(R.id.tvHaveAcct);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đăng ký...");

        tiEdtEmail.requestFocus();


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //inputs
                //validations

                String email = tiEdtEmail.getText().toString().trim();
                String password = tiEdtPassword.getText().toString().trim();

                setTextError(tilEmail, isCorrectEmail(email));
                setTextError(tilPassword, isCorrectEmail(password));
                if (isCorrectEmail(email) == null && isCorrectPassword(password) == null) {
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

    private String isCorrectEmail(String email) {
        if (email.isEmpty()) {
            return "Email không được để trống";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email không đúng định dạng!";
        } else {
            return null;
        }
    }

    private String isCorrectPassword(String password) {
        if (password.length() < 6) {
            return "Mật khẩu phải có tối thiểu 6 kí tự!";
        } else {
            return null;
        }
    }

    private void setTextError(TextInputLayout tilView, String messgage) {
        tilView.setError(messgage);
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

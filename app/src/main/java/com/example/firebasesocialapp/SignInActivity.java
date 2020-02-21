package com.example.firebasesocialapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    //views
    EditText tiEdtEmail, tiEdtPassword;
    Button btnSignIn;
    TextView tvNotHaveAcct;

    //progress dialog
    ProgressDialog progressDialog;

    //firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Action bar and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Đăng nhập");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init
        tiEdtEmail = findViewById(R.id.tiEdtEmail);
        tiEdtPassword = findViewById(R.id.tiEdtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvNotHaveAcct = findViewById(R.id.tvNotHaveAcct);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đăng nhập...");

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
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
                    tiEdtPassword.setError("Mật khẩu phải có tối thiểu 6 ký tự!");
                    tiEdtPassword.requestFocus();
                } else {
                    signInUser(email, password);
                }


            }
        });

        tvNotHaveAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

    }

    private void signInUser(String email, String password) {
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success => ProfileActivity
                            progressDialog.dismiss();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(SignInActivity.this, ProfileActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(SignInActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignInActivity.this, "Lỗi: " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go to previous activity
        return super.onSupportNavigateUp();
    }
}

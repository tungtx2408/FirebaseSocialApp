package com.example.firebasesocialapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    TextView tvNotHaveAcct, tvRecoverPassword;

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
        tvRecoverPassword = findViewById(R.id.tvRecoverPassword);
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

        tvRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });

    }

    private void showRecoverPasswordDialog() {

        final EditText edtEmail = new EditText(this);
        edtEmail.setHint("Email");
        edtEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edtEmail.setMinEms(16);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.addView(edtEmail);
        linearLayout.setPadding(10, 10, 10, 10);

        progressDialog.setMessage("Đang gửi email...");



        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Khôi phục mật khẩu!")
                .setView(linearLayout)
                .setPositiveButton("Gửi", null)
                .setNegativeButton("Hủy", null)
                .show();

        Button dialogPositiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        dialogPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmail.setError("Sai định dạng email!");
                } else {
                    sendEmail(email);
                }
            }
        });
    }

    private void sendEmail(String email) {
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "Gửi thành công!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(SignInActivity.this, "Thất bại...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void signInUser(String email, String password) {
        progressDialog.setMessage("Đăng nhập...");
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

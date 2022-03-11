package com.dinaldurak.mytrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinaldurak.mytrips.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText etLoginEmail, etLoginPassword, etRegisterEmail, etRegisterPassword,
            etRegisterNameSurname, etRegisterPhone;
    Button btnRegister, btnLogin;
    TextView txtHaveNotAccount, txtHaveAccount;
    LinearLayout loginLayout, registerLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        etRegisterEmail = findViewById(R.id.etSignupEmail);
        etRegisterPassword = findViewById(R.id.etSignupPassword);
        etRegisterNameSurname = findViewById(R.id.etNameSurname);
        etRegisterPhone = findViewById(R.id.etPhone);
        txtHaveNotAccount = findViewById(R.id.txt_signup);
        txtHaveAccount = findViewById(R.id.txt_login);
        loginLayout = findViewById(R.id.loginLayout);
        registerLayout = findViewById(R.id.signupLayout);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnSignup);

        firebaseAuth = FirebaseAuth.getInstance();

        //kayıt ol sayfasını görünür yapmak için
        txtHaveNotAccount.setOnClickListener(this);

        //giriş yap sayfasını görünür yapmak için
        txtHaveAccount.setOnClickListener(this);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_signup:
                loginLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_login:
                registerLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btnSignup:
                registerUser();
                break;
            case R.id.btnLogin:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etLoginEmail.setError("E-Posta Adresi girilmesi gereklidir.");
            etLoginEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etLoginPassword.setError("Parola girilmesi gereklidir.");
            etLoginPassword.requestFocus();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //email-adresi onaylamak için
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()){
                        //Main Activity'ye gitsin
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }else {
                        user.sendEmailVerification();
                    Toast.makeText(LoginActivity.this, "Hesabınızı doğrulamak için mail adresinizi kontrol ediniz!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Email veya parola hatalı! Tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerUser() {
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String nameSurname = etRegisterNameSurname.getText().toString().trim();
        String phone = etRegisterPhone.getText().toString().trim();

        if (email.isEmpty()) {
            etRegisterEmail.setError("E-Posta Adresi girilmesi gereklidir.");
            etRegisterEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etRegisterPassword.setError("Parola girilmesi gereklidir.");
            etRegisterPassword.requestFocus();
            return;
        }
        if (nameSurname.isEmpty()) {
            etRegisterNameSurname.setError("Ad Soyad girilmesi gereklidir.");
            etRegisterNameSurname.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            etRegisterPhone.setError("Cep Telefonu girilmesi gereklidir.");
            etRegisterPhone.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterEmail.setError("Geçerli bir e-posta adresi giriniz.");
            etRegisterEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etRegisterPassword.setError("Parola en az 6 karakterden oluşmalıdır.");
            etRegisterPassword.requestFocus();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    User user = new User(email, nameSurname, phone);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Kullanıcı başarılı bir şekilde kaydedildi!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Kullanıcı kaydedilemedi. Tekrar deneyin!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {

                    Toast.makeText(LoginActivity.this, "Kullanıcı kaydedilemedi. Tekrar deneyin!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
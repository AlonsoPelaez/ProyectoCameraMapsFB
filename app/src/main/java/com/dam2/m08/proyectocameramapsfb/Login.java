package com.dam2.m08.proyectocameramapsfb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private EditText usuario;
    private EditText password;
    private Button btn_login;
    private TextView sendToRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btn_login = findViewById(R.id.btnLogin);
        usuario = findViewById(R.id.edtxt_Usuario_Login);
        password = findViewById(R.id.edtxt_Contraseña_Login);
        sendToRegister = findViewById(R.id.sendToRegister);

        session();
        initUi();
    }
    public void initUi(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!usuario.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){

                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(usuario.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        //envia al home de la app
                                        showMaps();
                                    }
                                    else {
                                        //muestra error
                                        showAlertError(task.getException().getMessage());
                                    }
                                }
                            });
                }else {
                    showAlertError("los campos no pueden estar vacios. Intentelo de nuevo");
                }
            }
        });

        sendToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });
    }
    private void session(){
        SharedPreferences prefer= getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
        String email = prefer.getString("email",null);

        if (email != null){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);
        }
    }

    private void showAlertError(String message) {
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setMessage(message);
        alert.setCancelable(false);
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.create().show();
    }

    private void showMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("email",usuario.getText().toString());
        startActivity(intent);
    }
}

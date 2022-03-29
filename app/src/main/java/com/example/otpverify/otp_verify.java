package com.example.otpverify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.otpverify.databinding.ActivityOtpVerifyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class otp_verify extends AppCompatActivity {
    private ActivityOtpVerifyBinding binding;
    private String verificationId;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        binding = ActivityOtpVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId");

        binding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.verifyBtn.setVisibility(View.VISIBLE);
                if (binding.pinView.getText().toString().trim().isEmpty()) {
                    Toast.makeText(otp_verify.this, "OTP is not Valid!", Toast.LENGTH_SHORT).show();
                } else {
                    if (verificationId != null) {
                        String code = binding.pinView.getText().toString().trim();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        FirebaseAuth
                                .getInstance()
                                .signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            binding.verifyBtn.setVisibility(View.VISIBLE);
                                            Toast.makeText(otp_verify.this, "Welcome...", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(otp_verify.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            binding.verifyBtn.setVisibility(View.VISIBLE);
                                            Toast.makeText(otp_verify.this, "OTP is not Valid!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });


         long duration = TimeUnit.SECONDS.toMillis(10);
         new CountDownTimer(duration, 1000) {
             String sDuration;

             @Override
             public void onTick(long l) {
                 sDuration = String.format(Locale.ENGLISH, "%02d : %02d", TimeUnit.MILLISECONDS.toMinutes(l), TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                 binding.resendView.setVisibility(View.VISIBLE);
                 binding.resendView.setText(sDuration);
             }

             @Override
             public void onFinish() {
                 binding.resendView.setVisibility(View.VISIBLE);
                 binding.resendView.setText("Resend Otp");
                 Toast.makeText(getApplicationContext(), "Now You Can Resend Your OTP", Toast.LENGTH_SHORT).show();


             }
         }.start();
        binding.resendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(otp_verify.this, "sent", Toast.LENGTH_SHORT).show();
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(otp_verify.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCodeSent(@NonNull String newVerificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = newVerificationId;
                        Toast.makeText(otp_verify.this, "OTP is sent", Toast.LENGTH_SHORT).show();

                    }
                };

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber("+91" + getIntent().getStringExtra("phone"))
                                .setTimeout(10L, TimeUnit.SECONDS)
                                .setActivity(otp_verify.this)
                                .setCallbacks(mCallbacks)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }


        });

    }
}
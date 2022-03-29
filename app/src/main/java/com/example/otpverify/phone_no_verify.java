package com.example.otpverify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.otpverify.databinding.ActivityPhoneNoVerifyBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class phone_no_verify extends AppCompatActivity {
    private ActivityPhoneNoVerifyBinding binding;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no_verify);
        binding = ActivityPhoneNoVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        binding.sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.phoneNo.getText().toString().trim().isEmpty()) {
                    Toast.makeText(phone_no_verify.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                } else if (binding.phoneNo.getText().toString().trim().length() != 10) {
                    Toast.makeText(phone_no_verify.this, "Type valid Phone Number", Toast.LENGTH_SHORT).show();
                } else {
                    otpSend();
                }
            }
        });
    }

    private void otpSend() {
        binding.sendOtp.setVisibility(View.VISIBLE);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                binding.sendOtp.setVisibility(View.VISIBLE);
                Toast.makeText(phone_no_verify.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                binding.sendOtp.setVisibility(View.VISIBLE);
                Toast.makeText(phone_no_verify.this, "OTP is successfully send.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(phone_no_verify.this, otp_verify.class);
                intent.putExtra("phone", binding.phoneNo.getText().toString().trim());
                intent.putExtra("verificationId", verificationId);
                startActivity(intent);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber("+91" + binding.phoneNo.getText().toString().trim())
                        .setTimeout(10L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

}
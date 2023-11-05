package com.sirajul.lenscraft.utils;

import com.sirajul.lenscraft.DTO.SignupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpUtil {
    @Autowired
    JavaMailSender javaMailSender;
    public String generateOtp(){
        Random random = new Random();
        Integer otp = 100000 + random.nextInt(999999);
        return String.valueOf(otp);
    }

    public void sendEmail(String recipientEmail){
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(recipientEmail);

            message.setSubject("Registration Successful");
            message.setText("Thank you registering with Lenscraft /n /n" +
                    "Your account has been successfully registered.Welcome to our family /n" +
                    "Best regards from team.lenscraft@gmail.com");

            javaMailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendOtpEmail(String recipientEmail, String otp) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipientEmail);

        message.setSubject("OTP code for registration");

        message.setText("Your OTP code for registering in Lenscraft is : " + otp);

        javaMailSender.send(message);
    }catch (Exception e){
        e.printStackTrace();
    }

    }

    public void resendOtpEmail(String recipientEmail, String otp){
        try{
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(recipientEmail);

            message.setSubject("New OTP code for registeration");

            message.setText("Your new OTP code for registering in Lenscraft is : " + otp);

            javaMailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean isOtpValid(SignupDto signupDto, String otp){

        LocalDateTime otpGeneratedTime = signupDto.getOtpGeneratedTime();

        LocalDateTime currentTimeStamp = LocalDateTime.now();

        return signupDto.getOtp().equals(otp) && currentTimeStamp.isBefore(otpGeneratedTime.plusMinutes(2));
    }
}

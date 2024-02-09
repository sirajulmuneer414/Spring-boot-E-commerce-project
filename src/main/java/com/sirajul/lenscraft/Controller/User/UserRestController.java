package com.sirajul.lenscraft.Controller.User;

import com.sirajul.lenscraft.DTO.ToPassBoolean;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserRestController {

    @Autowired
    UserService userService;

    @GetMapping("/checker/referralCode")
    public ResponseEntity<ToPassBoolean> getReferralCodeChecker(
            @RequestParam("referralCode") String code
    ){
        ToPassBoolean toPassBoolean = new ToPassBoolean();

        log.info("inside referral checker");

        toPassBoolean.setCheck(userService.existsByReferralCode(code));

        System.out.println(toPassBoolean.isCheck());

        return new ResponseEntity<>(toPassBoolean, HttpStatus.OK);

    }


}

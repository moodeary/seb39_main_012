package com.team012.server.users.controller;

import com.team012.server.users.dto.CustomerSignUpRequestDto;
import com.team012.server.users.dto.UsersMessageResponseDto;
import com.team012.server.users.dto.CompanySignUpRequestDto;
import com.team012.server.users.service.UsersManageCompanyService;
import com.team012.server.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final UsersService usersService;

    private final UsersManageCompanyService usersManageCompanyService;

    // 회사 회원가입
    @PostMapping("/join/company")
    public ResponseEntity postCompany(@Valid @RequestBody CompanySignUpRequestDto dto) {
        usersManageCompanyService.createCompany(dto);
        UsersMessageResponseDto response = UsersMessageResponseDto
                .builder()
                .message("회원가입 완료..!")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 고객 회원가입
    @PostMapping("/join/customer")
    public ResponseEntity postCustomer(@Valid @RequestBody CustomerSignUpRequestDto dto) {
        usersService.createCustomer(dto);
        UsersMessageResponseDto response = UsersMessageResponseDto
                .builder()
                .message("회원가입 완료..!")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 이메일 중복확인
    @GetMapping("/check")
    public Boolean checkEmail(@RequestParam String email) {
        return usersService.getEmail(email);
    }

}

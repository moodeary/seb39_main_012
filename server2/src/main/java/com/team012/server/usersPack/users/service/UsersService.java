package com.team012.server.usersPack.users.service;

import com.team012.server.usersPack.users.repository.UsersRepository;
import com.team012.server.usersPack.users.dto.UsersDto;
import com.team012.server.usersPack.users.entity.Users;
import com.team012.server.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;

    private final ReservationRepository reservationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 이메일 중복체크
    public Boolean getEmail(String email) {
        Users users = usersRepository.findByEmail(email);
        return users != null;
    }

    // Company 회원가입..
    public Users createCompany(UsersDto.CompanyPost dto) {

        // 비밀번호 암호화
        String encPassword = bCryptPasswordEncoder.encode(dto.getPassword());

        // 객체에 반영
        Users savedCompanyUser
                = Users.builder()
                .email(dto.getEmail())
                .password(encPassword)
                .username(dto.getUsername())
                .phone(dto.getPhone())
                .roles("ROLE_COMPANY")
                .build();

        usersRepository.save(savedCompanyUser);
        // DB에 저장

        return savedCompanyUser;
    }

    // Customer 회원가입
    public Users createCustomer(UsersDto.CustomerPost dto) {
        // 회원이 있는지 없는지 체크

        // 비밀번호 암호화
        String encPassword = bCryptPasswordEncoder.encode(dto.getPassword());

        Users savedCustomer
                = Users.builder()
                .email(dto.getEmail())
                .password(encPassword)
                .username(dto.getUsername())
                .phone(dto.getPhone())
                .roles("ROLE_CUSTOMER")
                .build();

        // DB에 저장
        usersRepository.save(savedCustomer);

        return savedCustomer;
    }

}
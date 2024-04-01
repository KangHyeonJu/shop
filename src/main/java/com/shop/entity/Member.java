package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFromDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member")
@Getter @Setter
@ToString
public class Member {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFromDto memberFromDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFromDto.getName());
        member.setEmail(memberFromDto.getEmail());
        member.setAddress(memberFromDto.getEmail());
        String password = passwordEncoder.encode(memberFromDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.USER);
        return member;
    }

}
package com.stech.usermgmt.service.impl;

import org.springframework.stereotype.Service;

import com.stech.usermgmt.entity.UserDetailsEntity;
import com.stech.usermgmt.exception.CustomRuntimeException;
import com.stech.usermgmt.repository.UserDetailsRepository;
import com.stech.usermgmt.service.UserDetailsService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsRepository userRepository;


    UserDetailsServiceImpl(UserDetailsRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserDetailsEntity getUserDetails(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomRuntimeException("User not found"));
    }

    public void updateUserDetails(Long userId, UserDetailsEntity userDetails) {
        UserDetailsEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomRuntimeException("User not found"));
        user.setAddress1(userDetails.getAddress1());
        user.setAddress2(userDetails.getAddress2());
        user.setCity(userDetails.getCity());
        user.setState(userDetails.getState());
        user.setCountry(userDetails.getCountry());
        user.setPincode(userDetails.getPincode());
        userRepository.save(user);
    }
}

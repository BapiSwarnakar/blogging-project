package com.stech.usermgmt.service;

import com.stech.usermgmt.entity.UserDetailsEntity;

public interface UserDetailsService {
    
    public UserDetailsEntity getUserDetails(Long userId);
    public void updateUserDetails(Long userId, UserDetailsEntity userDetails);
}

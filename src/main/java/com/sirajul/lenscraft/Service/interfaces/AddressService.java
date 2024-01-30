package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.user.Address;

public interface AddressService {
    Address saveAndReturn(Address address);

    Address findById(Long addressId);

    void delete(Address address);

    void save(Address address);
}

package com.sirajul.lenscraft.Service.User;

import com.sirajul.lenscraft.Repository.AddressRepository;
import com.sirajul.lenscraft.Service.interfaces.AddressService;
import com.sirajul.lenscraft.entity.user.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImp implements AddressService {

    @Autowired
    AddressRepository addressRepository;
    @Override
    public Address saveAndReturn(Address address) {

        address.setActive(true);
        Address addressToReturn = addressRepository.save(address);

    return addressToReturn;
    }

    @Override
    public Address findById(Long addressId) {
        return addressRepository.findById(addressId).get();
    }

    @Override
    public void delete(Address address) {
        address.setActive(false);
        addressRepository.save(address);
    }

    @Override
    public void save(Address address) {
        address.setActive(true);
        addressRepository.save(address);
    }
}

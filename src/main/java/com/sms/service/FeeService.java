package com.sms.service;

import com.sms.model.Fee;
import com.sms.repository.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * FeeService — business logic for fee tracking and management.
 */
@Service
public class FeeService {

    @Autowired
    private FeeRepository feeRepository;

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public Fee saveFee(Fee fee) {
        fee.calculateStatus();
        return feeRepository.save(fee);
    }

    public List<Fee> getFeesByStudentId(Integer studentId) {
        return feeRepository.findByStudentId(studentId);
    }
}

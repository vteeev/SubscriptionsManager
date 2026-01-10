package com.example.subscriptionmanager.presentation.controller;

import com.example.subscriptionmanager.application.dto.CreateSubscriptionCommand;
import com.example.subscriptionmanager.application.dto.MonthlyCostDto;
import com.example.subscriptionmanager.application.dto.SubscriptionDto;
import com.example.subscriptionmanager.application.usecase.*;
import com.example.subscriptionmanager.infrastructure.security.CurrentUser;
import com.example.subscriptionmanager.presentation.mapper.SubscriptionMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for subscription management.
 */
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final AddSubscriptionUseCase addSubscriptionUseCase;
    private final CancelSubscriptionUseCase cancelSubscriptionUseCase;
    private final ListSubscriptionsUseCase listSubscriptionsUseCase;
    private final CalculateMonthlyCostUseCase calculateMonthlyCostUseCase;
    private final SubscriptionMapper mapper;

    public SubscriptionController(
            AddSubscriptionUseCase addSubscriptionUseCase,
            CancelSubscriptionUseCase cancelSubscriptionUseCase,
            ListSubscriptionsUseCase listSubscriptionsUseCase,
            CalculateMonthlyCostUseCase calculateMonthlyCostUseCase,
            SubscriptionMapper mapper) {
        this.addSubscriptionUseCase = addSubscriptionUseCase;
        this.cancelSubscriptionUseCase = cancelSubscriptionUseCase;
        this.listSubscriptionsUseCase = listSubscriptionsUseCase;
        this.calculateMonthlyCostUseCase = calculateMonthlyCostUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<SubscriptionMapper.SubscriptionResponse> createSubscription(
            @Valid @RequestBody SubscriptionMapper.CreateSubscriptionRequest request) {
        String userId = CurrentUser.getUserId();
        CreateSubscriptionCommand command = mapper.toCommand(request, userId);
        SubscriptionDto dto = addSubscriptionUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(dto));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionMapper.SubscriptionResponse>> listSubscriptions() {
        String userId = CurrentUser.getUserId();
        List<SubscriptionDto> dtos = listSubscriptionsUseCase.execute(userId);
        return ResponseEntity.ok(mapper.toResponseList(dtos));
    }

    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionMapper.SubscriptionResponse>> listActiveSubscriptions() {
        String userId = CurrentUser.getUserId();
        List<SubscriptionDto> dtos = listSubscriptionsUseCase.executeActive(userId);
        return ResponseEntity.ok(mapper.toResponseList(dtos));
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> cancelSubscription(@PathVariable String subscriptionId) {
        cancelSubscriptionUseCase.execute(subscriptionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cost/monthly")
    public ResponseEntity<SubscriptionMapper.MonthlyCostResponse> calculateMonthlyCost() {
        String userId = CurrentUser.getUserId();
        MonthlyCostDto dto = calculateMonthlyCostUseCase.execute(userId);
        return ResponseEntity.ok(mapper.toResponse(dto));
    }
}

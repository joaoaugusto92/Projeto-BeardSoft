package com.api.beard_soft.controller;

import com.api.beard_soft.dto.user.barbers.BarberRequestDto;
import com.api.beard_soft.dto.user.barbers.BarberResponseDto;
import com.api.beard_soft.service.BarberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbers")
public class BarberController {
    
    private final BarberService barberService;
    
    public BarberController(BarberService barberService) {
        this.barberService = barberService;
    }
    
    @GetMapping
    public ResponseEntity<List<BarberResponseDto>> getAllBarbers() {
        List<BarberResponseDto> barbers = barberService.findAllBarbers();
        return ResponseEntity.ok(barbers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BarberResponseDto> getBarberById(@PathVariable Long id) {
        BarberResponseDto barber = barberService.findBarberById(id);
        return ResponseEntity.ok(barber);
    }
    
    @PostMapping
    public ResponseEntity<BarberResponseDto> createBarber(@RequestBody @Valid BarberRequestDto requestDto) {
        BarberResponseDto barber = barberService.createBarber(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(barber);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BarberResponseDto> updateBarber(@PathVariable Long id, @RequestBody @Valid BarberRequestDto requestDto) {
        BarberResponseDto barber = barberService.updateBarber(id, requestDto);
        return ResponseEntity.ok(barber);
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<BarberResponseDto> deactivateBarber(@PathVariable Long id) {
        BarberResponseDto barber = barberService.deactivateBarber(id);
        return ResponseEntity.ok(barber);
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<BarberResponseDto> activateBarber(@PathVariable Long id) {
        BarberResponseDto barber = barberService.activateBarber(id);
        return ResponseEntity.ok(barber);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBarber(@PathVariable Long id) {
        barberService.deleteBarber(id);
        return ResponseEntity.noContent().build();
    }
}

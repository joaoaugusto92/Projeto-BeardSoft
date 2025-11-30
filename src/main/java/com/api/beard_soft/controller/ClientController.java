package com.api.beard_soft.controller;

import com.api.beard_soft.dto.user.clients.ClientRequestDto;
import com.api.beard_soft.dto.user.clients.ClientResponseDto;
import com.api.beard_soft.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    
    private final ClientService clientService;
    
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }
    
    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        List<ClientResponseDto> clients = clientService.findAllClients();
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
        ClientResponseDto client = clientService.findClientById(id);
        return ResponseEntity.ok(client);
    }
    
    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody @Valid ClientRequestDto requestDto) {
        ClientResponseDto client = clientService.createClient(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(@PathVariable Long id, @RequestBody @Valid ClientRequestDto requestDto) {
        ClientResponseDto client = clientService.updateClient(id, requestDto);
        return ResponseEntity.ok(client);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}

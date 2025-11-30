package com.api.beard_soft.controller;

import com.api.beard_soft.dto.user.services.ServiceRequestDto;
import com.api.beard_soft.dto.user.services.ServiceResponseDto;
import com.api.beard_soft.service.ServicesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServicesService servicesService;

    public ServiceController(ServicesService servicesService){
        this.servicesService = servicesService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> getAllServices(){
        List<ServiceResponseDto> services = servicesService.findAllServices();

        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getService(@PathVariable Long id){
        ServiceResponseDto response = servicesService.findServiceByID(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ServiceResponseDto> createService(@RequestBody @Valid ServiceRequestDto serviceRequest) {
        System.out.println(">>> Entrou no método createService() com sucesso!");
        ServiceResponseDto response = servicesService.createService(serviceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(@PathVariable Long id, @RequestBody @Valid ServiceRequestDto serviceRequest){
        ServiceResponseDto response = servicesService.updateService(id, serviceRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/services/deactivate/{id}")
    public ResponseEntity<ServiceResponseDto> desactivateService(@PathVariable Long id) {
        ServiceResponseDto response = servicesService.deactivateService(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/services/activate/{id}")
    public ResponseEntity<ServiceResponseDto> activateService(@PathVariable Long id){
        ServiceResponseDto activatedService = servicesService.activateService(id);
        return ResponseEntity.ok(activatedService);
    }

    @DeleteMapping("/admin/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        servicesService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

}

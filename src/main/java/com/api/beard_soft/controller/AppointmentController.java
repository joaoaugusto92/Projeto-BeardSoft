package com.api.beard_soft.controller;

import com.api.beard_soft.dto.user.Appointments.AppointmentsRequestDto;
import com.api.beard_soft.dto.user.Appointments.AppointmentsResponseDto;
import com.api.beard_soft.infra.security.CustomCLientDetails;
import com.api.beard_soft.service.AppointmentsService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentsService appointmentsService;

    public AppointmentController(AppointmentsService appointmentsService){
        this.appointmentsService = appointmentsService;
    }

    @PostMapping
    public ResponseEntity<AppointmentsResponseDto> createAppointment(@RequestBody @Valid AppointmentsRequestDto requestDto){
        System.out.println(">>> Creating appointment: " + requestDto);
        AppointmentsResponseDto responseDto = appointmentsService.createAppointment(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentsResponseDto>> getAllAppointmentsForAdmin(){
        List<AppointmentsResponseDto> appointments = appointmentsService.findAllAppointmentsForAdmin();
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/client")
    public ResponseEntity<List<AppointmentsResponseDto>> getAllAppointments(@AuthenticationPrincipal CustomCLientDetails cLientDetails){
       if (cLientDetails == null) {
           // Se não há autenticação, retorna todos para admin
           List<AppointmentsResponseDto> appointments = appointmentsService.findAllAppointmentsForAdmin();
           return ResponseEntity.status(HttpStatus.OK).body(appointments);
       }
       Long clientId = cLientDetails.getClientId();
       List<AppointmentsResponseDto> appointments = appointmentsService.findAllAppointments(clientId);
       return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<AppointmentsResponseDto>> getAppointmentsByDayOrWeek(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String period){
        List<AppointmentsResponseDto> appointments =
                appointmentsService.findAppointmentsByDayOrWeek(date, period);
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentsResponseDto> getAppointmentsDetails(@PathVariable Long id,
                                                                          Authentication authentication){
        AppointmentsResponseDto dto = appointmentsService.findAppointmentDetails(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            Authentication authentication) {
        appointmentsService.cancelAppointment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

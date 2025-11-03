package com.api.beard_soft.controller;

import com.api.beard_soft.dto.user.Appointments.AppointmentsRequestDto;
import com.api.beard_soft.dto.user.Appointments.AppointmentsResponseDto;
import com.api.beard_soft.infra.security.CustomCLientDetails;
import com.api.beard_soft.service.AppointmentsService;
import com.sun.jdi.connect.spi.TransportService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

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
        AppointmentsResponseDto responseDto = appointmentsService.createAppointment(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/client")
    public ResponseEntity<List<AppointmentsResponseDto>> getAllAppointments( @AuthenticationPrincipal CustomCLientDetails cLientDetails){
       Long clientId = cLientDetails.getClientId();

       List<AppointmentsResponseDto> appointments = appointmentsService.findAllAppointments(clientId);

       return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentsResponseDto>> getAppointmentsByDayOrWeek(// Parâmetros opcionais para filtrar por data/semana
                                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                                    @RequestParam(required = false) String period){
        List<AppointmentsResponseDto> appointments =
                appointmentsService.findAppointmentsByDayOrWeek(date, period);

        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @appointmentsService.isAppointmentOwner(#id, principal.id)")
    public ResponseEntity<AppointmentsResponseDto> getAppointmentsDetails(@PathVariable Long id,
                                                                          Authentication authentication){
        AppointmentsResponseDto dto = appointmentsService.findAppointmentDetails(id);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (@appointmentsService.isAppointmentOwner(#id, principal.id)")
    //O PreAuthorize garante que apenas ADMIN ou CLIENT acesse esse endpoint
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            Authentication authentication) {

        appointmentsService.cancelAppointment(id);

        // Retorna 204 No Content para indicar sucesso na operação DELETE/CANCEL
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

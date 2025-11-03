package com.api.beard_soft.service;

import com.api.beard_soft.domain.user.Baber.BarberEntity;
import com.api.beard_soft.domain.user.Client.ClientEntity;
import com.api.beard_soft.domain.user.appointments.AppointmentsEntity;
import com.api.beard_soft.domain.user.appointments.AppointmentsStatus;
import com.api.beard_soft.domain.user.service.ServiceEntity;
import com.api.beard_soft.dto.user.Appointments.AppointmentsRequestDto;
import com.api.beard_soft.dto.user.Appointments.AppointmentsRescheduleRequestDto;
import com.api.beard_soft.dto.user.Appointments.AppointmentsResponseDto;
import com.api.beard_soft.repository.AppointmentsRepository;
import com.api.beard_soft.repository.BarberRepository;
import com.api.beard_soft.repository.ClientRepository;
import com.api.beard_soft.repository.ServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("appointmentservice")
public class AppointmentsService {

    // --- CONSTANTES DE HORÁRIO COMERCIAL ---
    private static final LocalTime START_TIME = LocalTime.of(9, 0); // 09:00
    private static final LocalTime END_TIME = LocalTime.of(18, 0);   // 18:00
    // ----------------------------------------

    private final AppointmentsRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final ClientRepository clientRepository;
    private final BarberRepository barberRepository;

    public AppointmentsService(
            AppointmentsRepository appointmentRepository,
            ServiceRepository serviceRepository,
            ClientRepository clientRepository,
            BarberRepository barberRepository) {
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
        this.clientRepository = clientRepository;
        this.barberRepository = barberRepository;
    }


    // --- LÓGICA DE CRIAÇÃO DO AGENDAMENTO ---
    @Transactional
    public AppointmentsResponseDto createAppointment(AppointmentsRequestDto requestDto) {
        // 1. Carregar Entidades (Validação de Existência)
        ServiceEntity service = loadService(requestDto.serviceId());
        ClientEntity client = loadClient(requestDto.clientId());
        BarberEntity barber = loadBarber(requestDto.barberId());

        // 2. Cálculo de Horários
        LocalDateTime startTime = LocalDateTime.of(requestDto.date(), requestDto.time());
        Integer duration = service.getDurationInMinutes();
        LocalDateTime endTime = startTime.plusMinutes(duration);

        // 3. Validações de Negócio
        validateServiceActive(service);
        validateBusinessHours(startTime, endTime);
        validateAppointmentTimeConflict(startTime, endTime, barber);

        // 4. Criação e Persistência da Entidade
        AppointmentsEntity newAppointment = convertToEntity(client, barber, service, startTime, endTime, duration);

        AppointmentsEntity savedAppointment = appointmentRepository.save(newAppointment);

        // 5. Retorno do DTO de Resposta
        return convertToDto(savedAppointment);
    }

    public List<AppointmentsResponseDto> findAllAppointments(Long clientId){
        ClientEntity client = loadClient(clientId);

        List<AppointmentsEntity> appointmentsEntities = appointmentRepository.findByClientWithEagerData(client);
         return appointmentsEntities.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentsResponseDto> findAppointmentsByDayOrWeek(LocalDate date, String period){
        LocalDateTime start;
        LocalDateTime end;
        if (date != null) {
            start = date.atStartOfDay();

            if ("week".equalsIgnoreCase(period)) {
                // Filtra pela semana (ex: de Segunda a Domingo)
                LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
                LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);
                start = startOfWeek.atStartOfDay();
                end = endOfWeek.atTime(LocalTime.MAX);
            } else {
                end = date.atTime(LocalTime.MAX); // Até o final do dia
            }
        } else {
            // Se não houver filtro, retorna tudo
            List<AppointmentsEntity> allAppointments = appointmentRepository.findAll();
            return allAppointments.stream().map(this::convertToDto).collect(Collectors.toList());
        }

        // Buscar com filtro e JOIN FETCH para carregar os dados de Cliente/User
        List<AppointmentsEntity> filteredAppointments =
                appointmentRepository.findAppointmentsWithDetailsBetween(start, end);

        return filteredAppointments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isAppointmentOwner(Long appointmentId, Long clientId){
        return appointmentRepository.findById(appointmentId)
                .map(AppointmentsEntity::getClient) // Pega o ClientEntity do agendamento (LAZY LOAD seguro pelo @Transactional)
                .map(ClientEntity::getId)         // Pega o ID do Cliente (do agendamento)
                .map(ownerId -> ownerId.equals(clientId)) // Compara com o ID do cliente logado
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public AppointmentsResponseDto findAppointmentDetails(Long appointmentId) {
        // A lógica de segurança está no Controller. Aqui, apenas busca os dados.
        AppointmentsEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agendamento com ID " + appointmentId + " não encontrado."
                ));
        return convertToDto(appointment);
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {

        AppointmentsEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agendamento com ID " + appointmentId + " não encontrado."
                ));

        // Lógica de Negócio: Não se pode cancelar um agendamento já finalizado ou cancelado.
        if (appointment.getStatus() == AppointmentsStatus.CANCELED || appointment.getStatus() == AppointmentsStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Não é possível cancelar um agendamento com status: " + appointment.getStatus()
            );
        }

        // Altera o status
        appointment.setStatus(AppointmentsStatus.CANCELED);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public AppointmentsResponseDto rescheduleAppointment(Long appointmentId, AppointmentsRescheduleRequestDto requestDto) {

        //(Validação de Existência)
        AppointmentsEntity existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agendamento com ID " + appointmentId + " não encontrado."
                ));

        //O agendamento só pode ser reagendado se não tiver sido FINALIZADO.
        if (existingAppointment.getStatus() == AppointmentsStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Não é possível reagendar um agendamento finalizado."
            );
        }

        //Cálculo de Novos Horários
        LocalDateTime newStartTime = LocalDateTime.of(requestDto.newDate(), requestDto.newTime());
        Integer duration = existingAppointment.getDurationInMinutes(); // A duração do serviço é mantida
        LocalDateTime newEndTime = newStartTime.plusMinutes(duration);

        ServiceEntity service = existingAppointment.getService();
        BarberEntity barber = existingAppointment.getBarber(); // O barbeiro é o mesmo

        validateBusinessHours(newStartTime, newEndTime);

        validateAppointmentTimeConflictExcludingSelf(newStartTime, newEndTime, barber, appointmentId);

        // 5. Atualiza a Entidade
        existingAppointment.setStartTime(newStartTime);
        existingAppointment.setEndTime(newEndTime);
        existingAppointment.setStatus(AppointmentsStatus.PENDING); // O status pode voltar a PENDING após reagendamento.

        // Persiste a mudança (opcionalmente explícito, mas garantido pelo @Transactional)
        AppointmentsEntity updatedAppointment = appointmentRepository.save(existingAppointment);

        return convertToDto(updatedAppointment);
    }

    private void validateAppointmentTimeConflictExcludingSelf(
            LocalDateTime startTime,
            LocalDateTime endTime,
            BarberEntity barber,
            Long excludedAppointmentId) {

        List<AppointmentsStatus> activeStatuses = List.of(
                AppointmentsStatus.PENDING,
                AppointmentsStatus.CONFIRMED,
                AppointmentsStatus.IN_SERVICE
        );

        // Você precisará de uma nova query no Repository (ou um filtro no Java)
        // que exclua o 'excludedAppointmentId'.
        Optional<AppointmentsEntity> conflict = appointmentRepository
                .findConflictForBarberExcludingId(startTime, endTime, barber, activeStatuses, excludedAppointmentId);

        if (conflict.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "O Barbeiro já possui um agendamento conflitante no novo horário: " + startTime.toLocalTime()
            );
        }
    }


    // --- MÉTODOS DE CARREGAMENTO (LOAD) ---
    private ServiceEntity loadService(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Serviço com ID " + serviceId + " não encontrado!"
                ));
    }

    private ClientEntity loadClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente com ID " + clientId + " não encontrado!"
                ));
    }

    private BarberEntity loadBarber(Long barberId) {
        return barberRepository.findById(barberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Barbeiro com ID " + barberId + " não encontrado!"
                ));
    }


    // --- MÉTODOS DE VALIDAÇÃO ---
    private void validateServiceActive(ServiceEntity service) {
        if (Boolean.FALSE.equals(service.getIsActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O serviço '" + service.getName() + "' não está ativo e não pode ser agendado."
            );
        }
    }

    private void validateBusinessHours(LocalDateTime startTime, LocalDateTime endTime) {
        DayOfWeek dayOfWeek = startTime.toLocalDate().getDayOfWeek();
        LocalTime appointmentStartHour = startTime.toLocalTime();
        LocalTime appointmentEndHour = endTime.toLocalTime();

        // 1. Checagem do Dia da Semana
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A barbearia não funciona aos Domingos."
            );
        }

        // 2. Checagem do Horário de Início
        if (appointmentStartHour.isBefore(START_TIME) || appointmentStartHour.isAfter(END_TIME)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("O horário de início (%s) deve estar entre %s e %s.",
                            appointmentStartHour, START_TIME, END_TIME)
            );
        }

        // 3. Checagem do Horário de Término (não pode ultrapassar o horário de fechamento)
        if (appointmentEndHour.isAfter(END_TIME)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("O agendamento termina às %s, o que excede o horário de fechamento (%s).",
                            appointmentEndHour, END_TIME)
            );
        }
    }

    private void validateAppointmentTimeConflict(LocalDateTime startTime, LocalDateTime endTime, BarberEntity barber) {
        List<AppointmentsStatus> activeStatuses = List.of(
                AppointmentsStatus.PENDING,
                AppointmentsStatus.CONFIRMED,
                AppointmentsStatus.IN_SERVICE
        );

        Optional<AppointmentsEntity> conflict = appointmentRepository
                .findFirstConflictForBarber(startTime, endTime, barber, activeStatuses);

        if (conflict.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "O Barbeiro já possui um agendamento conflitante no horário de " + startTime.toLocalTime()
            );
        }
    }


    // --- MÉTODOS DE CONVERSÃO ---
    private AppointmentsEntity convertToEntity(ClientEntity client, BarberEntity barber, ServiceEntity service,
                                               LocalDateTime startTime, LocalDateTime endTime, Integer durationInMinutes) {

        AppointmentsEntity entity = new AppointmentsEntity();

        entity.setClient(client);
        entity.setBarber(barber);
        entity.setService(service);
        entity.setStartTime(startTime);
        entity.setEndTime(endTime);
        entity.setDurationInMinutes(durationInMinutes);
        entity.setStatus(AppointmentsStatus.PENDING); // Status inicial

        return entity;
    }

    private AppointmentsResponseDto convertToDto(AppointmentsEntity entity) {
        ClientEntity client = entity.getClient();
        BarberEntity barber = entity.getBarber();
        ServiceEntity service = entity.getService();

        // 1. Obter Nome/Email (assumindo que ClientEntity e BarberEntity têm acesso ao UserEntity)
        // NOTA: Certifique-se de que a BarberEntity também possui um UserEntity para pegar o nome
        String clientName = client.getUser().getName();
        String clientEmail = client.getUser().getEmail();
        String barberName = barber.getName();

        // 2. Valores calculados/formatados
        String durationDisplay = formatDuration(entity.getDurationInMinutes());
        String valueDisplay = formatCurrency(service.getValue());

        // A chamada ao construtor com os 15 campos na ordem correta:
        return new AppointmentsResponseDto(
                // 1. id
                entity.getId(),

                // 2-4. DADOS DO CLIENTE
                client.getId(),               // 2. clientId
                clientName,                   // 3. clientName
                clientEmail,                  // 4. clientEmail

                // 5-6. DADOS DO BARBEIRO
                barber.getId(),               // 5. barberId
                barberName,                   // 6. barberName

                // 7-12. DADOS DO SERVIÇO
                service.getId(),              // 7. serviceId
                service.getName(),            // 8. serviceName
                entity.getDurationInMinutes(),// 9. durationMinutes (Integer)
                durationDisplay,              // 10. serviceDurationDisplay (String)
                service.getValue(),           // 11. serviceValue (BigDecimal)
                valueDisplay,                 // 12. serviceValueDisplay (String)

                // 13-15. DADOS DO AGENDAMENTO
                entity.getStartTime(),        // 13. startTime (LocalDateTime)
                entity.getEndTime(),          // 14. endTime (LocalDateTime)
                entity.getStatus().toString() // 15. status (String)
        );
    }

    // ----------------------------------------------------------------------
    // --- MÉTODOS DE FORMATAÇÃO


    private String formatCurrency(BigDecimal value) {
        if (value == null) return null;
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return format.format(value);
    }

    private String formatDuration(Integer minutes) {
        if (minutes == null || minutes <= 0) return null;

        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours).append("h");
        }

        if (remainingMinutes > 0) {
            if (hours > 0) {
                sb.append(" ");
            }
            sb.append(remainingMinutes).append("m");
        }
        return sb.toString();
    }
}
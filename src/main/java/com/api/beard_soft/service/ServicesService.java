package com.api.beard_soft.service;

import com.api.beard_soft.domain.user.appointments.AppointmentsStatus;
import com.api.beard_soft.domain.user.service.ServiceEntity;
import com.api.beard_soft.dto.user.services.ServiceRequestDto;
import com.api.beard_soft.dto.user.services.ServiceResponseDto;
import com.api.beard_soft.repository.AppointmentsRepository;
import com.api.beard_soft.repository.ServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicesService {
    private final ServiceRepository serviceRepository;
    private final AppointmentsRepository appointmentRepository;

    public ServicesService(ServiceRepository serviceRepository, AppointmentsRepository appointmentRepository){
        this.serviceRepository = serviceRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<ServiceResponseDto> findAllServices() {
        List<ServiceEntity> servicesEntities = serviceRepository.findAll();
        return servicesEntities.stream()
                .map(this::convertToDTO) //mapeia cada elemento e aplica o método convertToDTO, convertendo entidades em ServiceResponseDTO
                .collect(Collectors.toList()); /* coleta todos os ServicesResponseDTO que estão fluindo
                    e os junta em uma nova List<ServiceResponseDTO> */
    }

    public ServiceResponseDto findServiceByID(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Serviço com ID " + id + " não encontrado!"
                ));

        return convertToDTO(entity);
    }

    public ServiceResponseDto createService(ServiceRequestDto requestDTO){
        ServiceEntity newEntity = convertToEntity(requestDTO);

        // Garante que isActive não seja nulo, se não foi especificado
        if (newEntity.getIsActive() == null) {
            newEntity.setIsActive(true);
        }

        ServiceEntity savedEntity = serviceRepository.save(newEntity);
        return convertToDTO(savedEntity);

    }

    public ServiceResponseDto updateService (Long id, ServiceRequestDto requestDTO){
        ServiceEntity entityToUpdate = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Serviço com ID " + id + " não encontrado!"
                ));
        String newName = requestDTO.name();
        String currentName = entityToUpdate.getName();

        // 1. Verifica se o nome no DTO é diferente do nome atual na Entidade
        if (newName != null && !newName.equals(currentName)) {

            // 2. Busca no repositório por um serviço com o 'newName', ignorando o 'id' atual
            Optional<ServiceEntity> existingService = serviceRepository.findByNameAndIdNot(newName, id);

            // 3. Se um outro serviço for encontrado, lança uma exceção
            if (existingService.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O nome do serviço '" + newName + "' já existe para outro serviço."
                );
            }
        }

        boolean hasActiveFutureAppointments = hasActiveFutureAppointments(entityToUpdate);

        // Checa se a duração foi alterada (e se o DTO forneceu um novo valor)
        if (hasActiveFutureAppointments){
            if (requestDTO.durationInMinutes() != null &&
                    !requestDTO.durationInMinutes().equals(entityToUpdate.getDurationInMinutes())) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Não é possível alterar a duração do serviço, pois ele possui agendamentos futuros ATIVOS."
                );
            }
            // Checa se o status ativo foi alterado para 'false' (desativação)
            // O segundo cheque (entityToUpdate.getIsActive()) evita erro se for nulo
            if (requestDTO.isActive() != null &&
                    !requestDTO.isActive() &&
                    (entityToUpdate.getIsActive() == null || entityToUpdate.getIsActive())) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Não é possível desativar o serviço, pois ele possui agendamentos futuros ATIVOS. Cancele-os primeiro."
                );
            }
        }



        updateEntityFromDTO(entityToUpdate, requestDTO);

        ServiceEntity updatedEntity = serviceRepository.save(entityToUpdate);

        return convertToDTO(updatedEntity);

    }

    public void deleteService(Long id){
        ServiceEntity entityToDelete = serviceRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Serviço com ID " + id +" não encontrado"));


        //impede a exlusão de serviços com qualquer agendamentos
        long totalAppointments = appointmentRepository.countByService(entityToDelete);
        if (totalAppointments > 0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível realizar exclusão definitiva do serviço, pois ele possui um histórico de "
                            + totalAppointments + " agendamento(s). Portanto, considere usar o endpoint de desativação (`desactivateService`)."
            );
        }

        serviceRepository.delete(entityToDelete);
    }

    public ServiceResponseDto deactivateService(Long id){
        ServiceEntity entityToUpdate = serviceRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Serviço com ID " + id +" não encontrado"));

        if (hasActiveFutureAppointments(entityToUpdate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível desativar o serviço pois ele possui agendamentos futuros ATIVOS. Cancele-os primeiro."
            );
        }
        entityToUpdate.setIsActive(false);
        ServiceEntity updatedEntity = serviceRepository.save(entityToUpdate);

        return convertToDTO(updatedEntity);
    }

    public ServiceResponseDto activateService(Long id){
        // 1. Busca a entidade, lançando exceção se não encontrar
        ServiceEntity entityToUpdate = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Serviço com ID " + id +" não encontrado"
                ));

        // 2. Define o status como ATIVO (true)
        entityToUpdate.setIsActive(true);

        // 3. Salva a entidade atualizada
        ServiceEntity updatedEntity = serviceRepository.save(entityToUpdate);

        // 4. Retorna o DTO
        return convertToDTO(updatedEntity);
    }


    private void updateEntityFromDTO(ServiceEntity entity, ServiceRequestDto dto) {
        entity.setName(dto.name());
        entity.setValue(dto.value());
        entity.setDescription(dto.description());
        entity.setDurationInMinutes(dto.durationInMinutes());
        entity.setImageUrl(dto.imageURL()); // Note: usar ImageURL se esse for o nome correto no seu DTO
        entity.setCategory(dto.category());

        // Atualiza isActive, mas checa se foi fornecido, caso contrário mantém o valor antigo
        if (dto.isActive() != null) {
            entity.setIsActive(dto.isActive());
        }

    }
    private ServiceResponseDto convertToDTO(ServiceEntity entity) {
        return new ServiceResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getValue(),
                formatCurrency(entity.getValue()),              // campo displayValue
                entity.getDescription(),
                entity.getDurationInMinutes(),
                formatDuration(entity.getDurationInMinutes()),  // campo displayDuration
                entity.getImageUrl(),
                entity.getCategory(),
                entity.getIsActive()
        );
    }

    private ServiceEntity convertToEntity(ServiceRequestDto dto) {
        ServiceEntity entity = new ServiceEntity();

        // O ID é deixado em branco, pois será gerado pelo banco de dados
        entity.setName(dto.name());
        entity.setValue(dto.value());
        entity.setDescription(dto.description());
        entity.setDurationInMinutes(dto.durationInMinutes());
        entity.setImageUrl(dto.imageURL());
        entity.setCategory(dto.category());
        entity.setIsActive(dto.isActive()); // Pode ser nulo

        return entity;
    }

    //Formata BidDecimal para a moeda BR (Ex: R$20,00)
    private String formatCurrency(BigDecimal value) {
        if (value == null) return null;
        // Locale para o Brasil para garantir "R$" e a vírgula.
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return format.format(value);
    }

    //Formata minutos inteiros para um formato de horas e minutos(Ex: 90 -> 1h30min)
    private String formatDuration(Integer minutes) {
        if (minutes == null || minutes <= 0) return null;

        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours).append("h");
        }

        if (remainingMinutes > 0) {
            // Adiciona espaço se já houver horas
            if (hours > 0) {
                sb.append(" ");
            }
            sb.append(remainingMinutes).append("m");
        }
        // Se a duração for exatamente 0, o DTO já retornaria null, mas garante
        // que o formato seja claro (Ex: "30m", "1h", "1h 30m").
        return sb.toString();
    }
    //Método auxiliar para verificar agendamentos futuros ATIVOS
    private boolean hasActiveFutureAppointments(ServiceEntity service) {

        // Lista de status que NÃO representam um agendamento futuro ativo
        List<AppointmentsStatus> inactiveStatus = List.of(
                AppointmentsStatus.CANCELLED,
                AppointmentsStatus.COMPLETED
        );

        long count = appointmentRepository.countByServiceAndStartTimeAfterAndStatusIsNotIn(
                service,
                LocalDateTime.now(), // Verifica a partir do momento atual
                inactiveStatus
        );

        return count > 0;
    }
}

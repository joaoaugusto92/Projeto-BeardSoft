package com.api.beard_soft.infra.security;

import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.repository.ClientRepository;
import com.api.beard_soft.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public CustomUserDetailsService(UserRepository userRepository, ClientRepository clientRepository){
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Busca o usuário no banco pelo email, se não encontrar, lança um erro.
        UserEntity user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if("CLIENT".equalsIgnoreCase(String.valueOf(user.getRole()))){
            Long clientId = clientRepository.findByUser(user)
                    .orElseThrow(() -> new UsernameNotFoundException("Client not found for this user"))
                    .getId();
            return new CustomCLientDetails(user, clientId);
        }
        else if ("ADMIN".equalsIgnoreCase(String.valueOf(user.getRole())) || "BARBER".equalsIgnoreCase(String.valueOf(user.getRole()))) {

            // Administradores e Barbeiros não têm um ClientEntity e não precisam do clientId.
            // Retornamos o próprio UserEntity, assumindo que ele implementa UserDetails,
            // ou você cria um CustomAdminDetails simples.
            // Para simplicidade, assumindo que UserEntity implementa UserDetails (ou as interfaces necessárias):
            return user;
        }

        // 3. Caso o Role não seja reconhecido ou o usuário esteja incompleto
        throw new UsernameNotFoundException("User role not recognized or invalid.");
    }
}

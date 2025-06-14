package com.ecommerce.order.clients;

import com.ecommerce.order.external.dtos.UsersDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<UsersDto> getUserById(String id) {
        try {
            return Optional.ofNullable(
                    restClient.get()
                            .uri("http://user-service/api/users/" + id)
                            .retrieve()
                            .body(UsersDto.class)
            );
        } catch (HttpClientErrorException.NotFound ex) {
            // 404 = product not found
            return Optional.empty();
        } catch (HttpClientErrorException ex) {
            // other HTTP errors (e.g. 500, 401)
            throw new RuntimeException("Error calling user service: " + ex.getMessage(), ex);
        }
    }
}

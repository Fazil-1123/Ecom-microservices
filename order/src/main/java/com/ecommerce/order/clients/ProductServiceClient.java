package com.ecommerce.order.clients;

import com.ecommerce.order.dtos.ReserveStockRequest;
import com.ecommerce.order.dtos.ReserveStockResponse;
import com.ecommerce.order.external.dtos.ProductDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class ProductServiceClient {

    private final RestClient restClient;

    public ProductServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<ProductDto> getProductById(Long id) {
        try {
            return Optional.ofNullable(
                    restClient.get()
                            .uri("http://product-service/api/products/" + id)
                            .retrieve()
                            .body(ProductDto.class)
            );
        } catch (HttpClientErrorException.NotFound ex) {
            // 404 = product not found
            return Optional.empty();
        } catch (HttpClientErrorException ex) {
            // other HTTP errors (e.g. 500, 401)
            throw new RuntimeException("Error calling product service: " + ex.getMessage(), ex);
        }
    }

    public ReserveStockResponse reserve(Long id, ReserveStockRequest body) {
        try {
            return restClient.post()
                    .uri("http://product-service/api/products/{id}/reserve", id)
                    .body(body)
                    .retrieve()
                    .body(ReserveStockResponse.class);
        } catch (HttpClientErrorException ex) {
            // Let OrderServiceImpl decide: 409 = retry, 422 = fail
            if (ex.getStatusCode() == HttpStatus.CONFLICT || ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                throw ex;
            }
            throw new RuntimeException("Error calling product service: " + ex.getMessage(), ex);
        }

    }
}

package com.ecommerce.order.clients;

import com.ecommerce.order.external.dtos.ProductDto;
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
    public void updateProductQuantity(ProductDto productDto, Long id){
        restClient.put()
                .uri("http://product-service/api/products/" + id)
                .body(productDto)
                .retrieve()
                .toBodilessEntity();

    }

}

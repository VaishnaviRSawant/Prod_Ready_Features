package com.prod_ready_features.clients.impl;

import com.prod_ready_features.advice.ApiResponse;
import com.prod_ready_features.clients.EmployeeClient;
import com.prod_ready_features.dto.EmployeeDTO;
import com.prod_ready_features.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeClientImpl implements EmployeeClient {

    private final RestClient restClient;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        try {
            ApiResponse<List<EmployeeDTO>> employeeDTOList = restClient.get()
                    .uri("employees")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {

                    });

            return employeeDTOList.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmployeeDTO getEmployeeById(Long employeeId) {
        try {
            ApiResponse<EmployeeDTO> employeeResponse = restClient.get()
                    .uri("employees/{employeeId}", employeeId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            return employeeResponse.getData();
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            System.out.println("Employee not found with ID: " + employeeId);
            return null;  // Handle the case where the employee isn't found
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public EmployeeDTO createNewEmployee(EmployeeDTO employeeDTO) {
        try{

            ApiResponse<EmployeeDTO> employeeDTOApiResponse = restClient.post()
                    .uri("employees")
                    .body(employeeDTO)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) ->{
                        System.out.println(new String( res.getBody(). readAllBytes()));
                        throw new ResourceNotFoundException("could not create the employee");
                    })

                    .body(new ParameterizedTypeReference<>() {
                    });
            return  employeeDTOApiResponse.getData();

        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}

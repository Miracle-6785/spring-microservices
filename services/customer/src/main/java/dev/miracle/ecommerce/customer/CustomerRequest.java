package dev.miracle.ecommerce.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(String id,
                              @NotNull(message = "Firstname is required")
                              String firstname,
                              @NotNull(message = "Lastname is required")
                              String lastname,
                              @NotNull(message = "Email is required")
                              @Email(message = "Email is invalid")
                              String email,
                              Address address) {
}

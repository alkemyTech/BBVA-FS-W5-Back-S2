package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.models.User;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserUpdatedDTO {

    private String firstName;
    private String lastName;
    private String password;

    public UserUpdatedDTO mapFromUser(final User user){
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
        return this;
    }


}


package com.elearning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter @Setter 
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "people")
public class Person {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Gender gender;
    private HomeAddress address;

    public Person(String fname, String lname, Date dob, Gender gender, HomeAddress address) {
        this.firstName = fname;
        this.lastName = lname;
        this.dateOfBirth = dob;
        this.gender = gender;
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return id != null && id.equals(person.id);
    }
}

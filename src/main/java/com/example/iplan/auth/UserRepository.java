package com.example.iplan.auth;

import com.example.iplan.Repository.DefaultFirebaseRepository.DefaultFirebaseDBRepository;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

@Repository
public class UsersRepository extends DefaultFirebaseDBRepository<Users>{
    public UsersRepository(Firestore firestore)
    {
        super(firestore);
        setEntityClass(Users.class);
        setCollectionName("User");
    }
}
